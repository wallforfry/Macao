package fr.esiee.bde.macao.Jobs;

/**
 * Created by Wallerand on 31/05/2017.
 */

public class Jobs {

    private int id;
    private String title;
    private String slug;
    private String category;
    private String color;
    private String name;
    private String email;
    private String telephone;
    private String content;

    public Jobs() {
    }

    public Jobs(int id, String title, String slug, String categorie, String color, String name, String email, String telephone, String content) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.category = categorie;
        this.color = color;
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.content = content;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
