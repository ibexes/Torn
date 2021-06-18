package com.torn.assistant.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"USER_ID", "LAST_ACTION"})})
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="USER_ID", nullable=false)
    private User user;

    @Column(name = "LAST_ACTION")
    private Date lastAction;

    private Date timestamp;

    public UserActivity() {

    }

    public UserActivity(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getLastAction() {
        return lastAction;
    }

    public void setLastAction(Date lastAction) {
        this.lastAction = lastAction;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
