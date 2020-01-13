package xyz.kida.magiccardseeker.presentation.search.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import xyz.kida.magiccardseeker.R;
import xyz.kida.magiccardseeker.data.api.models.MagicCard;
import xyz.kida.magiccardseeker.data.di.FakeDI;
import xyz.kida.magiccardseeker.presentation.search.SearchContract;
import xyz.kida.magiccardseeker.presentation.search.adapter.SearchAdapter;
import xyz.kida.magiccardseeker.presentation.search.mapper.MagicCardToViewModelMapper;
import xyz.kida.magiccardseeker.presentation.search.model.MagicCardOnSwitchListener;
import xyz.kida.magiccardseeker.presentation.search.model.MagicCardViewModel;
import xyz.kida.magiccardseeker.presentation.search.presenter.SearchPresenter;

public class SearchFragment extends Fragment implements SearchContract.View, MagicCardOnSwitchListener {

    @BindView(R.id.fragment_search_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.search_view)
    SearchView searchView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private List<MagicCard> cards;
    private Disposable disposable;
    private SearchAdapter adapter;
    SearchContract.Presenter presenter;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureSearchView();
        configureRecyclerView();
        //TODO : FAKE DI HERE
        presenter = new SearchPresenter(FakeDI.getMagicCardRepository(), new MagicCardToViewModelMapper());
        presenter.attachView(this);
    }



    private void configureSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private Timer timer = new Timer();

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    timer.cancel();
                    timer = new Timer();
                    int sleep = 1000;
                    if (newText.length() == 1) {
                        sleep = 50000;
                    } else if (newText.length() <= 3) {
                        sleep = 5000;
                    } else if (newText.length() <= 5) {
                        sleep = 3000;
                    }
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            presenter.searchCards(newText);
                        }
                    }, sleep);
                }
                return true;
            }
        });
    }

    private void configureRecyclerView() {
        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void showCards(List<MagicCardViewModel> magicCardViewModels) {
        progressBar.setVisibility(View.GONE);
        adapter.bindViewModels(magicCardViewModels);
    }

    @Override
    public void onSwitchToggle(String cardId, boolean isFavorite) {
        if (isFavorite) {
            presenter.addCardToCollection(cardId);
        } else {
            presenter.deleteCardFromCollection(cardId);
        }
    }

    @Override
    public void onCardAddToCollection() {

    }

    @Override
    public void onCardRemovedFromCollection() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.presenter.detachView();
    }
}