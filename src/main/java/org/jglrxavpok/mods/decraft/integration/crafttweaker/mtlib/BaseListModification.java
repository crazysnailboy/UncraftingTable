package org.jglrxavpok.mods.decraft.integration.crafttweaker.mtlib;

import java.util.LinkedList;
import java.util.List;


/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jared
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
public abstract class BaseListModification<T> extends BaseUndoable {
    protected final List<T> list;
    protected final LinkedList<T> recipes;
    protected final LinkedList<T> successful;

    protected BaseListModification(String name, List<T> list) {
        super(name);
        this.list = list;
        this.recipes = new LinkedList<T>();
        this.successful = new LinkedList<T>();
    }

    @Override
    public boolean canUndo() {
        if(recipes.isEmpty() || successful.isEmpty())
            return false;

        return true;
    }

    @Override
    protected String getRecipeInfo() {
        if(!recipes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for(T recipe : recipes) {
                if(recipe != null) {
                    sb.append(getRecipeInfo(recipe)).append(", ");
                }
            }

            if(sb.length() > 0) {
                sb.setLength(sb.length() - 2);
            }

            return sb.toString();
        }

        return super.getRecipeInfo();
    }

    /**
     * This method must be overwritten by the extending classes. It should return
     * the name of the key item, for which the recipe is for. For example for machines
     * which produce new items, it should return the name of the ouput. For machines
     * which are processing items (like a pulverizer) it should return the name of the
     * the input. Another example would be the name of the enchantmant for a thaumcraft
     * infusion recipe.
     */
    protected abstract String getRecipeInfo(T recipe);

    @Override
    public int hashCode() {
        return 37 * super.hashCode() ^ (list != null ? 43 * list.hashCode() : 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;

        if(obj == null)
            return false;

        // Do both actions have the same base?
        if(!super.equals(obj))
            return false;

        if(!(obj instanceof BaseListModification<?>))
            return false;

        BaseListModification<?> mod = (BaseListModification<?>) obj;

        // Do both actions reference the same list?
        if(this.list != mod.list) {
            return false;
        }

        // Do both actions contain the same recipes?
        if(recipes.size() != mod.recipes.size()) {
            return false;
        }

        for(T recipe : recipes) {
            boolean found = false;
            for(T otherRecipe : (List<T>) mod.recipes) {
                if(equals(recipe, otherRecipe)) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares two recipes if they are equal. If the mod does not overwrite equals() in
     * their recipe object, this method should be overwritten by the extending classes to
     * add a custom equals method
     */
    protected boolean equals(T recipe, T otherRecipe) {
        if(recipe == otherRecipe) {
            return true;
        }

        if(!recipe.equals(otherRecipe)) {
            return false;
        }

        return true;
    }

    public String getJEICategory(T recipe){
        return null;
    }
}
