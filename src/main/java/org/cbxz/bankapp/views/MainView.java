package org.cbxz.bankapp.views;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.Setter;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.client.ClientsRepository;
import org.cbxz.bankapp.views.Client.ClientEditor;
import org.cbxz.bankapp.views.Client.CreditOfferEditor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@Route("")
public class MainView extends VerticalLayout {

  private ClientsRepository clientsRepository;

  private Grid<Client> grid = new Grid<>();
  private final TextField passportFilter = new TextField("", "Фильтр");
  private final TextField lastNameFilter = new TextField("", "Фильтр по фамилии");
  private final Button addNewBtn = new Button("Добавить клиента");
  private final Button editClientButton = new Button("Изменить");
  private final Button deleteButton = new Button("Удалить");
  private final Button createCreditOffer = new Button("Оформить на клиента кредит");
  private final Button creditsButton = new Button("Кредиты");
  private final Button banksButton = new Button("Банк");
  private final HorizontalLayout tools = new HorizontalLayout(passportFilter, lastNameFilter,
      addNewBtn, editClientButton, deleteButton, createCreditOffer);
  private final HorizontalLayout navigationBar = new HorizontalLayout(creditsButton, banksButton);
  private final ClientEditor clientEditor;
  private final CreditOfferEditor creditOfferEditor;

  @Setter
  private ChangeHandler changeHandler;


  public interface ChangeHandler {

    void onChange();
  }

  @Autowired
  public MainView(ClientsRepository clientsRepository, ClientEditor clientEditor,
      CreditOfferEditor creditOfferEditor) {
    this.clientsRepository = clientsRepository;
    this.clientEditor = clientEditor;
    this.creditOfferEditor = creditOfferEditor;
    gridFiller();
    tools.setSpacing(true);
    creditsButton.getElement().getThemeList().add("primary");
    banksButton.getElement().getThemeList().add("primary");
    addNewBtn.setIcon(VaadinIcon.PLUS.create());
    createCreditOffer.setEnabled(false);
    createCreditOffer.setIcon(VaadinIcon.PIGGY_BANK_COIN.create());
    editClientButton.setEnabled(false);
    deleteButton.setEnabled(false);
    deleteButton.setIcon(VaadinIcon.TRASH.create());
    editClientButton.setIcon(VaadinIcon.PENCIL.create());
    tools.setJustifyContentMode(JustifyContentMode.START);
    tools.setSizeFull();
    tools.setAlignItems(Alignment.START);
    navigationBar.setSizeFull();
    navigationBar.setJustifyContentMode(JustifyContentMode.CENTER);
    lastNameFilter.addValueChangeListener(this::lastNameFilter);
    lastNameFilter.setValueChangeMode(ValueChangeMode.EAGER);
    lastNameFilter.addValueChangeListener(e->showClient(e.getValue()));
    passportFilter.setWidth("250px");
    passportFilter.addValueChangeListener(this::passportFilter);
    passportFilter.setValueChangeMode(ValueChangeMode.EAGER);
    passportFilter.addValueChangeListener(e -> showClient(e.getValue()));
    passportFilter.setPlaceholder("Фильтрация по паспорту");
    passportFilter.setValueChangeMode(ValueChangeMode.EAGER);
    add(tools, grid, navigationBar, clientEditor);
  }

  private void buttonsListeners() {
    grid.addSelectionListener(e -> {
      if (!grid.asSingleSelect().isEmpty()) {
        createCreditOffer.setEnabled(true);
        editClientButton.setEnabled(true);
        deleteButton.setEnabled(true);
      } else {
        createCreditOffer.setEnabled(false);
        deleteButton.setEnabled(false);
        editClientButton.setEnabled(false);
      }
    });
    addNewBtn.addClickListener(e -> {
      clientEditor.open();
      clientEditor.editClient(new Client());
    });
    editClientButton.addClickListener(e -> {
      clientEditor.open();
      clientEditor.editClient(grid.asSingleSelect().getValue());
    });
    deleteButton.addClickListener(e -> {
      deleteClient(grid.asSingleSelect().getValue());
      changeHandler.onChange();
      deleteButton.setEnabled(false);
    });
    createCreditOffer.addClickListener(e -> {
      creditOfferEditor.open();
    });
    creditsButton.addClickListener(e -> {
      creditsButton.getUI().ifPresent(ui -> {
        ui.navigate("credits");
      });
    });
    createCreditOffer.addClickListener(e -> {
      creditOfferEditor.setClient(grid.asSingleSelect().getValue());
      creditOfferEditor.open();
    });
    banksButton.addClickListener(e -> {
      banksButton.getUI().ifPresent(ui -> {
        ui.navigate("bank");
      });
    });
  }



  private void deleteClient(Client client) {
    if (clientsRepository.findById(client.getId()).isPresent() && !client.getCreditOfferSet()
        .isEmpty()) {
      Notification.show("У клиента есть кредиты").setPosition(Notification.Position.MIDDLE);
    } else {
      System.out.println("Клиент удален");
      clientsRepository.deleteById(client.getId());
    }
  }

  private void showClient(String firstName) {
    Iterable<Client> clientIterable = clientsRepository.findAll();
    List<Client> clientList = new ArrayList<>();
    if (firstName.isEmpty()) {
      for (Client client : clientIterable) {
        clientList.add(client);
      }
      grid.setItems(clientList);
    } else {
      Optional<Client> clientOptional = clientsRepository.findByFirstName(firstName);
      clientOptional.ifPresent(client -> grid.setItems(client));
    }
  }

  private void passportFilter(HasValue.ValueChangeEvent<String> stringValueChangeEvent) {
    ListDataProvider<Client> dataProvider = (ListDataProvider<Client>) grid.getDataProvider();
    dataProvider.setFilter(Client::getPassportNumber, passport ->
        (passport.toString()).contains(stringValueChangeEvent.getValue().toLowerCase()));
  }

  private void lastNameFilter(HasValue.ValueChangeEvent<String> stringValueChangeEvent) {
    ListDataProvider<Client> dataProvider = (ListDataProvider<Client>) grid.getDataProvider();
    dataProvider.setFilter(Client::getLastName, lastName ->
        lastName.toLowerCase().contains(stringValueChangeEvent.getValue().toLowerCase()));
  }

  private void gridFiller() {
    grid.setHeight("80vh");
    grid.addColumn(Client::getFirstName).setHeader("Имя");
    grid.addColumn(Client::getLastName).setHeader("Фамилия");
    grid.addColumn(Client::getPatronymic).setHeader("Отчество");
    grid.addColumn(Client::getEmail).setHeader("Email");
    grid.addColumn(Client::getPassportNumber).setHeader("Паспорт");
    grid
        .asSingleSelect()
        .addValueChangeListener(AbstractField.ComponentValueChangeEvent::getValue);
    clientEditor.setChangeHandler(() -> {
      showClient(passportFilter.getValue());
    });
    creditOfferEditor.setChangeHandler(() -> {
      showClient(passportFilter.getValue());
    });
    setChangeHandler(() -> {
      showClient(passportFilter.getValue());
    });
    showClient("");
    buttonsListeners();
  }
}
