package org.cbxz.bankapp.views.Bank;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.Route;
import lombok.Setter;
import org.cbxz.bankapp.models.Bank.Bank;
import org.cbxz.bankapp.models.Bank.BankRepository;

import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.credit.CreditRepository;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;
import org.cbxz.bankapp.models.creditOffer.CreditOfferRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Route("bank")
public class BankView extends VerticalLayout{

    private BankRepository bankRepository;

    private CreditOfferRepo creditOfferRepo;

    private Grid<Bank> grid = new Grid<>(Bank.class);
    private final TextField filter = new TextField("", "Фильтр");
    private final Button deleteButton = new Button("Удалить");
    private final Button showCreditDetails = new Button("Детали кредита");
    private final Button creditsButton = new Button("Кредиты");
    private final Button clientsButton = new Button("Клиенты");
    private final HorizontalLayout tools = new HorizontalLayout(filter, deleteButton, showCreditDetails);
    private final HorizontalLayout navigationBar = new HorizontalLayout(creditsButton, clientsButton);


    @Setter
    private ChangeHandler changeHandler;


    public interface ChangeHandler{
        void onChange();
    }


    @Autowired
    public BankView(BankRepository bankRepository, CreditOfferRepo creditOfferRepo){
        this.creditOfferRepo = creditOfferRepo;
        this.bankRepository = bankRepository;
        grid.setHeight("80vh");
        grid.setColumns("client", "credit");
//        grid.addColumn(CreditOffer::getClient).setHeader("Клиент");
//        grid.addColumn(CreditOffer::getCredit).setHeader("Кредит");
        tools.setSpacing(true);
        showCreditDetails.setIcon(VaadinIcon.OPEN_BOOK.create());
        showCreditDetails.setEnabled(false);
        deleteButton.setEnabled(false);
        deleteButton.setIcon(VaadinIcon.TRASH.create());
        tools.setJustifyContentMode(JustifyContentMode.START);
        tools.setSizeFull();
        tools.setAlignItems(Alignment.START);
        navigationBar.setSizeFull();
        navigationBar.setJustifyContentMode(JustifyContentMode.CENTER);
        add(tools, grid, navigationBar);
        grid
                .asSingleSelect()
                .addValueChangeListener(AbstractField.ComponentValueChangeEvent::getValue);
        showBanks();
        buttonsListeners();

    }
    private void buttonsListeners(){
        creditsButton.addClickListener(e->{
            creditsButton.getUI().ifPresent(ui->{
                ui.navigate("credits");
            });
        });
        clientsButton.addClickListener(e->{
            clientsButton.getUI().ifPresent(ui->{
                ui.navigate("");
            });
        });
    }
//    private void deleteClient(Client client){
//        if (clientsRepository.findById(client.getId()).isPresent()&&!client.getCreditOfferSet().isEmpty()){
//            Notification.show("У клиента есть кредиты").setPosition(Notification.Position.MIDDLE);
//
//        }
//        else {
//            System.out.println("Клиент удален");
//            clientsRepository.deleteById(client.getId());
//        }
//    }


    private void showBanks(){
        Iterable<Bank> bankIterable = bankRepository.findAll();
        List<Bank>  bankList= new ArrayList<>();
        for (Bank bank : bankIterable){
                bankList.add(bank);
        }
        grid.setItems(bankList);
    }
}

