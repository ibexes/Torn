package com.torn.assistant.service;

import com.torn.api.model.faction.AttackLog;
import com.torn.assistant.persistence.dao.AttackLogDao;
import com.torn.assistant.persistence.dao.FactionDao;
import com.torn.assistant.persistence.entity.Faction;
import de.raik.webhook.Webhook;
import de.raik.webhook.WebhookBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.torn.api.client.FactionApiClient.getAttacksFull;
import static com.torn.api.client.FactionApiClient.getAttacksFullBetween;
import static com.torn.assistant.utils.CollectionUtils.getRandomElement;

@Service
public class FactionAttackMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(FactionAttackMonitorService.class);
    private final FactionDao factionDao;
    private final AttackLogDao attackLogDao;
    private final WebhookService webhookService;
    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm:ss";
    private final DateFormat dateFormat = new SimpleDateFormat(DATE);
    private final DateFormat timeFormat = new SimpleDateFormat(TIME);

    @Autowired
    public FactionAttackMonitorService(FactionDao factionDao, AttackLogDao attackLogDao, WebhookService webhookService) {
        this.factionDao = factionDao;
        this.attackLogDao = attackLogDao;
        this.webhookService = webhookService;
    }


    private static String getProfileUrl(Long id) {
        return "https://www.torn.com/profiles.php?XID=" + id;
    }

    private static String getFactionUrl(Long id) {
        return "https://www.torn.com/factions.php?step=profile&ID=" + id;
    }

    @Transactional
    public void fetchUpdatesBetween(String from, String to) {
        for (Faction faction : factionDao.findByTrackAttacksTrue()) {
            logger.info("Manually updating attacks for {}", faction.getName());
            if (faction.getApiKey().isEmpty()) {
                logger.warn("There are no api keys for {}", faction.getName());
                continue;
            }

            try {
                List<AttackLog> attackLogList = getAttacksFullBetween(getRandomElement(faction.getApiKey()), from, to);
                attackLogList.sort(Comparator.comparing(AttackLog::getInitiated));
                process(faction, attackLogList);
            } catch (Exception ignored) {

            }
        }
    }

    @Scheduled(cron = "${ATTACK_MONITOR_CRON:*/30 * * * * ?}")
    @Transactional
    public void fetchUpdates() {
        for (Faction faction : factionDao.findByTrackAttacksTrue()) {
            logger.info("Updating attacks for {}", faction.getName());
            if (faction.getApiKey().isEmpty()) {
                logger.warn("There are no api keys for {}", faction.getName());
                continue;
            }

            try {
                List<AttackLog> attackLogList = getAttacksFull(getRandomElement(faction.getApiKey()));
                attackLogList.sort(Comparator.comparing(AttackLog::getInitiated));
                process(faction, attackLogList);
            } catch (Exception ignored) {

            }
        }
    }

    private void process(Faction faction, List<AttackLog> attackLogList) {
        for (AttackLog attackLog : attackLogList) {
            Optional<com.torn.assistant.persistence.entity.AttackLog> dbAttackLog = attackLogDao.findByLog(attackLog.getLog());
            if (dbAttackLog.isPresent()) {
                continue;
            }

            com.torn.assistant.persistence.entity.AttackLog newAttackLog =
                    new com.torn.assistant.persistence.entity.AttackLog();
            newAttackLog.setAttackerFaction(attackLog.getAttackerFaction());
            newAttackLog.setAttackerId(attackLog.getAttackerId());
            newAttackLog.setLog(attackLog.getLog());
            newAttackLog.setDefenderFaction(attackLog.getDefenderFaction());
            newAttackLog.setDefenderId(attackLog.getDefenderId());
            newAttackLog.setInitiated(attackLog.getInitiated());
            newAttackLog.setStealth(attackLog.getStealth());
            newAttackLog.setAttackType(attackLog.getAttackType());
            newAttackLog.setFairFight(attackLog.getFairFight());
            newAttackLog.setChain(attackLog.getChain());
            newAttackLog.setRespect(attackLog.getRespect());

            attackLogDao.save(newAttackLog);

            // we should move sending to webhook to execute after the transaction commit
            for (String webhookUrl : faction.getAttacksWebhooks()) {
                String attackLogUrl = "https://www.torn.com/loader.php?sid=attackLog&ID=" + attackLog.getLog();
                String attacker = attackLog.getAttackerId() == 0? "Somebody" : "[" + attackLog.getAttacker() + " \\[" + attackLog.getAttackerId() + "\\]](" + getProfileUrl(attackLog.getAttackerId()) +")";
                String defender = "[" + attackLog.getDefender() + " \\[" + attackLog.getDefenderId() + "\\]](" + getProfileUrl(attackLog.getDefenderId()) + ")";

                if (attackLog.getAttackerFaction().equals(faction.getId())) {
                    Webhook webhook = new WebhookBuilder(webhookUrl)
                            .content("[" + dateFormat.format(attackLog.getInitiated()) + "](" + attackLogUrl + ") " +
                                    timeFormat.format(attackLog.getInitiated())+ " **" +  attacker + "** " +
                                    attackLog.getAttackType() + " " + defender +
                                    (attackLog.getDefenderFaction().equals(0L)? "" : " of [" + attackLog.getDefenderFactionName() + " \\["+attackLog.getDefenderFaction()+"\\]]("+getFactionUrl(attackLog.getDefenderFaction())+")")
                            )
                            .build();
                    webhookService.enqueue(webhookUrl, webhook);
                } else {
                    Webhook webhook = new WebhookBuilder(webhookUrl)
                            .content("[" + dateFormat.format(attackLog.getInitiated()) + "](" + attackLogUrl + ") " +
                                    timeFormat.format(attackLog.getInitiated()) + " " +  defender + " " +
                                    attackLog.getAttackType().getInverted() + " **" + (attacker.equals("Somebody")? attacker.toLowerCase() : attacker) + "**" +
                                    (attackLog.getAttackerFaction().equals(0L) ? "" : " of [" + attackLog.getAttackerFactionName() + " \\["+attackLog.getAttackerFaction()+"\\]]("+getFactionUrl(attackLog.getAttackerFaction())+")")
                            )
                            .build();
                    webhookService.enqueue(webhookUrl, webhook);
                }
            }
        }
    }
}
