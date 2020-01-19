package xyz.kida.magiccardseeker.presentation.search;

import xyz.kida.magiccardseeker.presentation.model.MagicCardViewModel;

import java.util.List;

public interface SearchContract {

    interface View {
        void showCards(List<MagicCardViewModel> magicCardViewModels);

        void onCardAddToCollection();

        void onCardRemovedFromCollection();
    }

    interface Presenter {
        void searchCards(String keywords);

        void attachView(View view);

        void cancelSubscription();

        void addCardToCollection(MagicCardViewModel magicCardViewModel);

        void deleteCardFromCollection(String cardId);

        void detachView();
    }
}
