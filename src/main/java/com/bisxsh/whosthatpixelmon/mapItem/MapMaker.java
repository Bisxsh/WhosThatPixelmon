package com.bisxsh.whosthatpixelmon.mapItem;

import com.bisxsh.whosthatpixelmon.Whosthatpixelmon;
import com.github.ericliucn.realmap.images.ImageSaveTask;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

public class MapMaker {

    private ItemStack hiddenMap, revealedMap;
    private String fileName;
    private String pokemonForm, pokemonName;
    private File chosenSprite;
    private Whosthatpixelmon mainClass;


    public MapMaker(Whosthatpixelmon mainClaass) throws IOException, URISyntaxException {
        this.mainClass = mainClaass;
        this.generateMapsAndDetails();
    }

    public MapMaker() {

    }

    private void generateMapsAndDetails() throws IOException, URISyntaxException {
        getRandomPokemon();

        String dexNumber = fileName.substring(0, 3);
        try {
            pokemonForm = fileName.substring(4, chosenSprite.getName().length()-4);
            pokemonForm = pokemonForm.replace("-", " ");
        } catch (Exception e) {
            //Pokemon does not have a form
        }

        EnumSpecies enumPokemon = EnumSpecies.getFromDex(Integer.parseInt(dexNumber));
        pokemonName = enumPokemon.getPokemonName();
        //Fixes compound pokemon names, e.g. MrMime -> Mr Mime, without interrupting on hyphenated names
        //e.g. Porygon-Z
        final int pokemonNameLength = pokemonName.length();
        for (int i = 1; i < pokemonNameLength; i++) {
            char character = pokemonName.charAt(i);
            if (Character.isUpperCase(character) && Character.valueOf(pokemonName.charAt(i-1)) != '-') {
                StringBuilder compoundNameBuilder = new StringBuilder(pokemonName.substring(0,i))
                        .append(" ").append(pokemonName.substring(i));
                pokemonName = compoundNameBuilder.toString();
                i = pokemonName.length()+1;
            }
        }
        //

        generateMaps();
    }

    private void getRandomPokemon() throws IOException {

        Asset fileNamesAsset = mainClass.getFileNamesAsset();
        List<String> fileNames = fileNamesAsset.readLines();
        Random rand = new Random();
        String randomFile = fileNames.get(rand.nextInt(fileNames.size()));
        Asset pokemonSpriteAsset = mainClass.getSpriteAsset(randomFile);
        fileName = pokemonSpriteAsset.getFileName();

        File spriteDirectory = new File("config/sprites");
        if (!spriteDirectory.exists()) {
            spriteDirectory.mkdirs();
        }
        pokemonSpriteAsset.copyToDirectory(spriteDirectory.toPath());

        String spritePath = new StringBuilder(spriteDirectory.getPath().toString())
                .append("/").append(fileName).toString();
        chosenSprite = new File(spritePath);

    }


    private void generateMaps() throws IOException {
        //Get colour for map
        Color gray = new Color(160, 160, 160);
        int grayRGB = gray.getRGB();
        //


        //Change pixels on hidden and revealed pixelmon sprites
        BufferedImage hiddenImage = ImageIO.read(chosenSprite);
        BufferedImage revealedImage = ImageIO.read(chosenSprite);
        final int imageHeight = hiddenImage.getHeight();
        final int imageWidth = hiddenImage.getWidth();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                Color pixelColor = new Color(hiddenImage.getRGB(x,y));
                if (pixelColor.getRed() == 0 &&
                        pixelColor.getGreen() == 0 &&
                        pixelColor.getBlue() == 0) {
                    hiddenImage.setRGB(x, y, grayRGB);//Sets black pixels to gray
                    revealedImage.setRGB(x, y, grayRGB);//Sets black pixels to gray
                } else {
                    hiddenImage.setRGB(x, y, 4);//Sets black pixels to gray
                }

            }
        }
        //

        //Sets the map texture to the Pixelmon
        ImageSaveTask hiddenImageSaveTask = new ImageSaveTask(hiddenImage);
        ImageSaveTask revealedImageSaveTask = new ImageSaveTask(revealedImage);
        net.minecraft.item.ItemStack hiddenMapIn = hiddenImageSaveTask.getItemStack();
        net.minecraft.item.ItemStack revealedMapIn = revealedImageSaveTask.getItemStack();
        //

        hiddenMap = addMapLore(hiddenMapIn);
        revealedMap = addMapLore(revealedMapIn);

    }

    private ItemStack addMapLore(net.minecraft.item.ItemStack mapIn) {
        //Add lore to the maps
        ItemStack map = ItemStackUtil.fromNative(mapIn);

        final Text loreText = Text.of(TextColors.GOLD, TextStyles.ITALIC, "Who's That Pixelmon?");
        final LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        lore.add(0, loreText);
        loreData.set(lore);

        map.offer(lore);
        return map;
        //
    }

    public List<Text> getLore() {
        final Text loreText = Text.of(TextColors.GOLD, TextStyles.ITALIC, "Who's That Pixelmon?");
        final LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        lore.add(0, loreText);
        ItemStack item = ItemStack.builder()
                .itemType(ItemTypes.FILLED_MAP).build();
        item.offer(lore);
        return item.get(Keys.ITEM_LORE).get();
    }

    public void deleteSprite() {
        if (!chosenSprite.delete()) {
            mainClass.getLogger().warn("Sprite file was not deleted");
        }
    }

    public ItemStack getHiddenMap() {
        return hiddenMap;
    }

    public ItemStack getRevealedMap() {
        return revealedMap;
    }

    public String getPokemonForm() {
        return pokemonForm;
    }

    public String getPokemonName() {
        return pokemonName;
    }
}
