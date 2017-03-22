package translator;

import translator.model.TileGrid;

/**
 * Created by time on 3/22/17.
 */
public interface TranslationStrategy {
    TileGrid apply(TileGrid grid);
}
