package translator;

import translator.model.TileGrid;

/**
 * Created by time on 3/22/17.
 */
public class SimpleTranslation implements TranslationStrategy {
    @Override
    public TileGrid apply(TileGrid grid) {
        TileGrid highRes = new TileGrid(grid.size()*10);
        return highRes;
    }
}
