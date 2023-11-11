package com.example.mabco.Classes;

public class ProductSpecs {
    String spec_title,spec_desc,spec_image;

    public ProductSpecs(String spec_title, String spec_desc, String spec_image) {
        this.spec_title = spec_title;
        this.spec_desc = spec_desc;
        this.spec_image = spec_image;
    }

    public String getSpec_title() {
        return spec_title;
    }

    public void setSpec_title(String spec_title) {
        this.spec_title = spec_title;
    }

    public String getSpec_desc() {
        return spec_desc;
    }

    public void setSpec_desc(String spec_desc) {
        this.spec_desc = spec_desc;
    }

    public String getSpec_image() {
        return spec_image;
    }

    public void setSpec_image(String spec_image) {
        this.spec_image = spec_image;
    }
}
