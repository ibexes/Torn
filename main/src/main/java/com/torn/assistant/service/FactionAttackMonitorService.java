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
import static com.torn.assistant.utils.CollectionUtils.getRandomElement;

@Service
public class FactionAttackMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(FactionAttackMonitorService.class);
    private final FactionDao factionDao;
    private final AttackLogDao attackLogDao;
    private final WebhookService webhookService;
    private static final String pattern = "dd/MM/yyyy HH:mm:ss";
    private final DateFormat df = new SimpleDateFormat(pattern);

    @Autowired
    public FactionAttackMonitorService(FactionDao factionDao, AttackLogDao attackLogDao, WebhookService webhookService) {
        this.factionDao = factionDao;
        this.attackLogDao = attackLogDao;
        this.webhookService = webhookService;
    }


    private static String getProfileUrl(Long id) {
        return "https://www.torn.com/profiles.php?XID=" + id;
    }

    @Scheduled(cron = "${ATTACK_MONITOR_CRON:0 */1 * * * ?}")
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

                    attackLogDao.save(newAttackLog);

                    // we should move sending to webhook to execute after the transaction commit
                    for (String webhookUrl : faction.getAttacksWebhooks()) {
                        String attackLogUrl = "https://www.torn.com/loader.php?sid=attackLog&ID=" + attackLog.getLog();
                        String attacker = attackLog.getAttackerId()==0? "Somebody" : "[" + attackLog.getAttacker() + " \\[" + attackLog.getAttackerId() + "\\]](" + getProfileUrl(attackLog.getAttackerId()) +")";
                        String defender = "[" + attackLog.getDefender() + " \\[" + attackLog.getDefenderId() + "\\]](" + getProfileUrl(attackLog.getDefenderId()) + ")";
                        Webhook webhook = new WebhookBuilder(webhookUrl)
                                .content(df.format(attackLog.getInitiated()) + " " +  attacker + " " +
                                        attackLog.getAttackType() + " " + defender + " [(log)]("+attackLogUrl+")")
                                .build();
                        webhookService.enqueue(webhookUrl, webhook);
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}
