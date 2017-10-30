/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nrz.fairhandlerservice.model;

import nrz.fairhandlerservice.jpa.Pruduct;
import nrz.fairhandlerservice.jpaController.PruductJpaController;

/**
 *
 * @author rahimAdmin
 */
public class ProductModel {

    private static final PruductJpaController pruductJpaController = AbstractModel.getPruductJpaController();

    public void createProduct(Pruduct pruduct) {
        pruductJpaController.create(pruduct);
    }

    public Pruduct findPruductWithCode(String codeArticle) {
        Object result = pruductJpaController.findPruductWithCode(codeArticle);
        if (result.equals(-1)) {
            return null;
        } else {
            return (Pruduct) result;
        }
    }

    public boolean isPruductExist(String codeArticle) {
        return this.findPruductWithCode(codeArticle) != null;
    }

    public boolean isProductByCodeExist(String productCode) {
        return pruductJpaController.isProductByCodeExist(productCode);
    }

}
