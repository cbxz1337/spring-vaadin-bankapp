package org.cbxz.bankapp.views.Credit;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.Setter;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.credit.CreditRepository;
import org.cbxz.bankapp.models.creditOffer.CreditOfferRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@Route("credits")
public class CreditView extends VerticalLayout {

  private CreditRepository creditRepository;

  private Grid<Credit> grid = new Grid<>();
  private final TextField filter = new TextField("", "Фильтр");
  private final Button addNewBtn = new Button("Добавить кредит");
  private final Button editСreditButton = new Button("Изменить");
  private final Button deleteButton = new Button("Удалить");
  private final Button clientsButton = new Button("Клиенты");
  private final Button creditsButton = new Button("Кредиты");
  private final Button banksButton = new Button("Банк");
  private final HorizontalLayout tools = new HorizontalLayout(filter, addNewBtn, editСreditButton,
      deleteButton);
  private final HorizontalLayout navigationBar = new HorizontalLayout(creditsButton, banksButton,
      clientsButton);
  private final CreditEditor creditEditor;
  private final CreditOfferRepo creditOfferRepo;


  @Setter
  private ChangeHandler changeHandler;


  public interface ChangeHandler {

    void onChange();
  }


  @Autowired
  public CreditView(CreditRepository creditRepository, CreditEditor creditEditor,
      CreditOfferRepo creditOfferRepo) {
    this.creditRepository = creditRepository;
    this.creditEditor = creditEditor;
    this.creditOfferRepo = creditOfferRepo;
    grid.setHeight("80vh");
    grid.addColumn(Credit::getLimit).setHeader("Лимит");
    grid.addColumn(Credit::getPercent).setHeader("%");
    tools.setSpacing(true);
    addNewBtn.setIcon(VaadinIcon.PLUS.create());
    creditsButton.getElement().getThemeList().add("primary");
    banksButton.getElement().getThemeList().add("primary");
    clientsButton.getElement().getThemeList().add("primary");
    creditsButton.setEnabled(false);
    editСreditButton.setEnabled(false);
    deleteButton.setEnabled(false);
    deleteButton.setIcon(VaadinIcon.TRASH.create());
    editСreditButton.setIcon(VaadinIcon.PENCIL.create());
    tools.setJustifyContentMode(JustifyContentMode.START);
    tools.setSizeFull();
    tools.setAlignItems(Alignment.START);
    navigationBar.setSizeFull();
    navigationBar.setJustifyContentMode(JustifyContentMode.CENTER);
    filter.setValueChangeMode(ValueChangeMode.EAGER);
    filter.addValueChangeListener(e -> showCredits(e.getValue()));
    add(tools, grid, navigationBar, creditEditor);
    grid
        .asSingleSelect()
        .addValueChangeListener(AbstractField.ComponentValueChangeEvent::getValue);
    creditEditor.setChangeHandler(() -> {
      showCredits("");
    });
    setChangeHandler(() -> {
      showCredits("");
    });
    showCredits("");
    buttonsListeners();

  }

  private void buttonsListeners() {
    grid.addSelectionListener(e -> {
      if (!grid.asSingleSelect().isEmpty()) {
        editСreditButton.setEnabled(true);
        deleteButton.setEnabled(true);
      } else {
        editСreditButton.setEnabled(false);
      }
    });
    addNewBtn.addClickListener(e -> {
      creditEditor.open();
      creditEditor.editCredit(new Credit());
    });
    editСreditButton.addClickListener(e -> {
      creditEditor.open();
      creditEditor.editCredit(grid.asSingleSelect().getValue());
    });
    deleteButton.addClickListener(e -> {
      deleteCredit(grid.asSingleSelect().getValue());
      changeHandler.onChange();
      deleteButton.setEnabled(false);
    });
    clientsButton.addClickListener(e -> {
      clientsButton.getUI().ifPresent(ui -> {
        ui.navigate("");
      });
    });
    banksButton.addClickListener(e -> {
      banksButton.getUI().ifPresent(ui -> {
        ui.navigate("bank");
      });
    });
  }

  private void deleteCredit(Credit credit) {
    try {
      if (credit.getCreditOffers().size() == 0) {
        creditRepository.deleteById(credit.getId());
      } else {
        Notification.show("Кредит привязан как минимум к одному кредитному предложению.")
            .setPosition(Notification.Position.MIDDLE);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Notification.show("Что-то пошло не так!");
    }
  }

  private void showCredits(String limit) {
    Iterable<Credit> clientIterable = creditRepository.findAll();
    List<Credit> creditList = new ArrayList<>();
    if (limit.isEmpty()) {
      for (Credit credit : clientIterable) {
        creditList.add(credit);
      }
      grid.setItems(creditList);
    }
  }
}
