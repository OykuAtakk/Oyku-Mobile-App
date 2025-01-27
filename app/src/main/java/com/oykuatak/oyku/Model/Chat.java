package com.oykuatak.oyku.Model;

public class Chat {

    private String messageIcerigi,gonderen,alici,docId;

    public Chat(String messageIcerigi, String gonderen, String alici, String docId) {
        this.messageIcerigi = messageIcerigi;
        this.gonderen = gonderen;
        this.alici = alici;
        this.docId = docId;
    }

    public Chat() {
    }

    public String getMessageIcerigi() {
        return messageIcerigi;
    }

    public void setMessageIcerigi(String messageIcerigi) {
        this.messageIcerigi = messageIcerigi;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getAlici() {
        return alici;
    }

    public void setAlici(String alici) {
        this.alici = alici;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
