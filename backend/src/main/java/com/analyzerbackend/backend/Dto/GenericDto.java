package com.analyzerbackend.backend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericDto {
    private String name;
    private String ingredient;
    private String strength;
    private String form;

    @JsonProperty("unit_price")
    private Double unitPrice;

    private Double saving;

    @JsonProperty("match_verified")
    private Boolean matchVerified;

    public GenericDto() {
    }

    public GenericDto(String name, String ingredient, String strength, String form,
                      Double unitPrice, Double saving, Boolean matchVerified) {
        this.name = name;
        this.ingredient = ingredient;
        this.strength = strength;
        this.form = form;
        this.unitPrice = unitPrice;
        this.saving = saving;
        this.matchVerified = matchVerified;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String ingredient;
        private String strength;
        private String form;
        private Double unitPrice;
        private Double saving;
        private Boolean matchVerified;

        public Builder name(String name) { this.name = name; return this; }
        public Builder ingredient(String ingredient) { this.ingredient = ingredient; return this; }
        public Builder strength(String strength) { this.strength = strength; return this; }
        public Builder form(String form) { this.form = form; return this; }
        public Builder unitPrice(Double unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder saving(Double saving) { this.saving = saving; return this; }
        public Builder matchVerified(Boolean matchVerified) { this.matchVerified = matchVerified; return this; }

        public GenericDto build() {
            return new GenericDto(name, ingredient, strength, form, unitPrice, saving, matchVerified);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getForm() { return form; }
    public void setForm(String form) { this.form = form; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getSaving() { return saving; }
    public void setSaving(Double saving) { this.saving = saving; }

    public Boolean getMatchVerified() { return matchVerified; }
    public void setMatchVerified(Boolean matchVerified) { this.matchVerified = matchVerified; }
}
