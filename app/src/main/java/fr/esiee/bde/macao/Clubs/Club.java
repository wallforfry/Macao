package fr.esiee.bde.macao.Clubs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Wallerand on 01/06/2017.
 */

public class Club {
    private int id;
    private String title, image = "https://bde.esiee.fr/bundles/applicationbde/img/couverture.png", content, email, shortcode;

    public Club(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortcode() {
        return this.shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }
}
