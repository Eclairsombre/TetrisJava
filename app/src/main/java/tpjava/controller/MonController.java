package tpjava.controller;

import tpjava.model.MonModel;
import tpjava.vue.MonVue;

public class MonController {
    private MonModel model;
    private MonVue vue;

    public MonController(MonModel model, MonVue vue) {
        this.model = model;
        this.vue = vue;
    }
}
