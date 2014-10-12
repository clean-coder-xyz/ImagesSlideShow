package imagesslideshow.learning.cleancoder.com.imagesslideshow.slideshow;

import android.view.View;

/**
 * Created by lsemenov on 12.10.2014.
 */
public interface SlideShowOption<ItemType, TransformedItemType, RendererViewType extends View> {
    TransformedItemType transform(ItemType what);
    void render(TransformedItemType itemToRender, RendererViewType view);
    void releaseResources(ItemType item, TransformedItemType transformedItem);
}
