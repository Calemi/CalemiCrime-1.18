package com.tm.calemicrime.datagen;

import com.tm.calemicrime.main.CCReference;
import com.wildcard.buddycards.core.BuddycardsAPI;
import com.wildcard.buddycards.item.BuddycardItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CardModelGen extends ItemModelProvider {

    public CardModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, CCReference.MOD_ID, existingFileHelper);
    }

    protected void registerModels() {

        for (BuddycardItem card : BuddycardsAPI.getAllCards()) {

            if (card.getSet().getName().equals("crime")) {
                this.genCardModel(card.getSet().getName(), card.getCardNumber());
            }
        }
    }

    void genCardModel(String setName, int cardNum) {
        ResourceLocation location = new ResourceLocation(CCReference.MOD_ID, "item/buddycard_" + setName + cardNum);
        ItemModelBuilder card = this.factory.apply(location).parent(this.factory.apply(new ResourceLocation("buddycards", "item/buddycard"))).texture("layer0", new ResourceLocation(CCReference.MOD_ID, "items/" + setName + "_set/" + cardNum));
        card.override().predicate(new ResourceLocation("grade"), 1.0F).model(this.genGradedCardModel(setName, cardNum, 1));
        card.override().predicate(new ResourceLocation("grade"), 2.0F).model(this.genGradedCardModel(setName, cardNum, 2));
        card.override().predicate(new ResourceLocation("grade"), 3.0F).model(this.genGradedCardModel(setName, cardNum, 3));
        card.override().predicate(new ResourceLocation("grade"), 4.0F).model(this.genGradedCardModel(setName, cardNum, 4));
        card.override().predicate(new ResourceLocation("grade"), 5.0F).model(this.genGradedCardModel(setName, cardNum, 5));
        this.generatedModels.put(location, card);
    }

    ModelFile genGradedCardModel(String setName, int cardNum, int grade) {
        ResourceLocation location = new ResourceLocation(CCReference.MOD_ID, "item/buddycard_" + setName + cardNum + "_g" + grade);
        ItemModelBuilder card = this.factory.apply(location).parent(this.factory.apply(new ResourceLocation("buddycards", "item/buddycard"))).texture("layer0", new ResourceLocation(CCReference.MOD_ID, "items/" + setName + "_set/" + cardNum));
        this.generatedModels.put(location, card.texture("layer1", new ResourceLocation("buddycards", "item/grade" + grade)));
        return card;
    }
}
