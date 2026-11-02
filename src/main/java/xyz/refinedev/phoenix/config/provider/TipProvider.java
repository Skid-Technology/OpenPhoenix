package xyz.refinedev.phoenix.config.provider;

import xyz.refinedev.lib.config.provider.TypeProvider;
import xyz.refinedev.lib.config.provider.context.LoadContext;
import xyz.refinedev.lib.config.provider.context.SaveContext;
import xyz.refinedev.phoenix.config.Tip;

import java.util.ArrayList;
import java.util.List;

public class TipProvider implements TypeProvider<Tip> {

    public Tip load(LoadContext loadContext) {
        Object object = loadContext.getObject();
        ArrayList<String> values = new ArrayList<>();
        if (object instanceof List) {
            for (Object result : (List) object) {
                values.add(String.valueOf(result));
            }
        } else if (object != null) {
            values.add(String.valueOf(object));
        }
        return new Tip(values);
    }

    public Object save(SaveContext<Tip> saveContext) {
        return new ArrayList<>(((Tip) saveContext.getObject()).getLines());
    }
}
