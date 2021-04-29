package com.mikedeejay2.simplestack.system.itemclick.preprocess;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.PreprocessorBase;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.global.PreprocessCurseOfBinding;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.global.PreprocessResultSlot;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.global.PreprocessShulkerBox;

public class ItemClickPreprocessor extends PreprocessorBase<ItemClickPreprocess, ItemClickInfo, ItemClickPreprocessor>
{
    public ItemClickPreprocessor(Simplestack plugin)
    {
        super(plugin);
    }

    @Override
    public void initDefault()
    {
        PreprocessItemClick itemClick = new PreprocessItemClick(plugin);
        itemClick.initDefault();
        addPreprocess(itemClick);
        addPreprocess(new PreprocessResultSlot());
        addPreprocess(new PreprocessCurseOfBinding());
        addPreprocess(new PreprocessShulkerBox());
    }
}
