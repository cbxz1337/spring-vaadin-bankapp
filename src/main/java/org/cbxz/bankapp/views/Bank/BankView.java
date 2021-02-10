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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Route("bank")
public class BankView extends VerticalLayout{

    private BankRepository bankRepository;

    private Grid<Bank> grid = new Grid<>();
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
    public BankView(BankRepository bankRepository){
        this.bankRepository = bankRepository;
        grid.setHeight("80vh");
        grid.addColumn(Bank::getClient).setHeader("Клиент");
        grid.addColumn(Bank::getCredit).setHeader("Кредит");
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
//        buttonsListeners();

    }
//    private void buttonsListeners(){
//        grid.addSelectionListener(e->{
//            if(!grid.asSingleSelect().isEmpty()){
//                createCreditOffer.setEnabled(true);
//                editClientButton.setEnabled(true);
//                deleteButton.setEnabled(true);
//            }
//            else {
//                createCreditOffer.setEnabled(false);
//                deleteButton.setEnabled(false);
//                editClientButton.setEnabled(false);
//            }
//        });
//        addNewBtn.addClickListener(e->{
//            clientEditor.open();
//            clientEditor.editClient(new Client());
//        });
//        editClientButton.addClickListener(e->{
//            clientEditor.open();
//            clientEditor.editClient(grid.asSingleSelect().getValue());
//        });
//        deleteButton.addClickListener(e->{
//            deleteClient(grid.asSingleSelect().getValue());
//            changeHandler.onChange();
//            deleteButton.setEnabled(false);
//        });
//        createCreditOffer.addClickListener(e->{
//            creditOfferEditor.open();
//        });
//        creditsButton.addClickListener(e->{
//            creditsButton.getUI().ifPresent(ui->{
//                ui.navigate("credits");
//            });
//        });
//        createCreditOffer.addClickListener(e->{
//            creditOfferEditor.setClient(grid.asSingleSelect().getValue());
//            creditOfferEditor.open();
//        });
//
//    }
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
        Iterable<Bank> clientIterable = bankRepository.findAll();
        List<Bank> bankList = new ArrayList<>();
        for (Bank bank : clientIterable){
                bankList.add(bank);

            grid.setItems(bank);
        }
//        else {
//            Optional<Client> clientOptional = clientsRepository.findByFirstName(firstName);
//            clientOptional.ifPresent(client -> grid.setItems(client));
//        }
    }
}
