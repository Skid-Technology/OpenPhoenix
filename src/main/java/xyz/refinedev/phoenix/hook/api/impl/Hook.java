package xyz.refinedev.phoenix.hook.api.impl;

import org.geysermc.floodgate.api.FloodgateApi;

import xyz.refinedev.phoenix.Phoenix;
import xyz.refinedev.phoenix.dialog.bedrock.BedrockFormRenderer;
import xyz.refinedev.phoenix.hook.api.IHook;
import xyz.refinedev.phoenix.utils.BedrockUtils;

public class Hook extends IHook {

    @Override
    public String getRequiredPlugin() {
        return "floodgate";
    }

    @Override
    public void register(Phoenix phoenix) {
        BedrockUtils.setPredicate(result -> FloodgateApi.getInstance().isFloodgatePlayer(result));
        if (phoenix.getDialogHandler() != null) {
            phoenix.getDialogHandler().setBedrockDialogRenderer(new BedrockFormRenderer(phoenix));
        }
        phoenix.log("Registered Floodgate (Bedrock) support for Phoenix.");
    }
}
