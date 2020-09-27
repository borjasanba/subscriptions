package com.adidas.challenge.email.content;

public interface EmailContent {

    String getRecipients();

    String getSubject();

    String getBody();

    String getPlainBody();


}
