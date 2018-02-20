package com.example.kitstasher.objects;

/**
 * Created by Алексей on 14.05.2017.
 */

public class KsUser {
    private String name,
            accountType,
            loggedBy,
            socialId,
            appHqId,
            parseId,
            userpicUrl;

    private KsUser(KsUserBuilder ksUserBuilder) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(String loggedBy) {
        this.loggedBy = loggedBy;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getAppHqId() {
        return appHqId;
    }

    public void setAppHqId(String appHqId) {
        this.appHqId = appHqId;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public String getUserpicUrl() {
        return userpicUrl;
    }

    public void setUserpicUrl(String userpicUrl) {
        this.userpicUrl = userpicUrl;
    }

    public boolean saveToLocalDb() {
        return false;
    }

    public boolean registerInAppHq() {
        return false;
    }

    public boolean registerInParse() {
        return false;
    }

    public static class KsUserBuilder {
        private String name,
                accountType,
                loggedBy,
                socialId,
                appHqId,
                parseId,
                userpicUrl;

        public KsUserBuilder hasName(String name) {
            this.name = name;
            return this;
        }

        public KsUserBuilder hasAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public KsUserBuilder hasLoggedBy(String loggedBy) {
            this.loggedBy = loggedBy;
            return this;
        }

        public KsUserBuilder hasSocialId(String socialId) {
            this.socialId = socialId;
            return this;
        }

        public KsUserBuilder hasAppHqId(String appHqId) {
            this.appHqId = appHqId;
            return this;
        }

        public KsUserBuilder hasPatseId(String parseId) {
            this.parseId = parseId;
            return this;
        }

        public KsUserBuilder hasUserpicUrl(String userpicUrl) {
            this.userpicUrl = userpicUrl;
            return this;
        }
    }
}
