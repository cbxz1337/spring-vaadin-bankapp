package org.cbxz.bankapp.views.Client;


import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.cbxz.bankapp.models.Bank.Bank;
import org.cbxz.bankapp.models.Bank.BankRepository;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;
import org.cbxz.bankapp.models.creditOffer.CreditOfferRepo;
import org.cbxz.bankapp.models.creditOffer.CreditService;
import org.cbxz.bankapp.models.schedule.Schedule;
import org.cbxz.bankapp.models.schedule.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringComponent
@UIScope
public class CreditOfferEditor extends Dialog implements KeyNotifier {

  private BankRepository bankRepository;

  private VerticalLayout mainLayout = new VerticalLayout();

  private CreditOfferRepo creditOfferRepo;

  private CreditService creditService;

  private ScheduleRepo scheduleRepo;

  private Client client;

  private TextField creditAmount = new TextField();

  private TextField years;

  private HorizontalLayout firstStage = new HorizontalLayout();

  private Button accept = new Button("Подтвердить", VaadinIcon.CHECK.create());

  private Button cancel = new Button("Отмена");

  private VerticalLayout verticalLayout = new VerticalLayout();

  private VerticalLayout chooseCreditView = new VerticalLayout();

  private Grid<Credit> creditGrid;

  private VerticalLayout detailsWindow;
  private final Label title = new Label("Подбор кредита");

  @Setter
  private ChangeHandler changeHandler;

  public interface ChangeHandler {

    void onChange();
  }

  @Autowired
  public CreditOfferEditor(CreditOfferRepo creditOfferRepo, CreditService creditService,
      ScheduleRepo scheduleRepo, BankRepository bankRepository) {
    this.bankRepository = bankRepository;
    this.creditService = creditService;
    this.creditOfferRepo = creditOfferRepo;
    this.scheduleRepo = scheduleRepo;
    setCloseOnOutsideClick(false);
    title.getElement().getStyle()
        .set("border", "1px solid white")
        .set("font-weigth", "bold")
        .set("font-size", "20")
        .set("border-radius", "10px")
        .set("background", "#6e5d5c")
        .set("padding-bottom", "6px")
        .set("width", "150px")
        .set("text-align", "center");
    verticalLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    creditAmount.setPlaceholder("Введите сумму");
    firstStage.add(creditAmount, accept, cancel);
    verticalLayout.add(title, firstStage);
    accept.getElement().getThemeList().add("primary");
    cancel.getElement().getThemeList().add("primary");
    firstStage.setSizeFull();
    add(verticalLayout);
    createListeners();
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public void findCredit() {
    try {
      long desirableAmount = Long.parseLong(creditAmount.getValue());
      Iterable<Credit> creditIterable = creditService.findAll();
      List<Credit> creditList = new ArrayList<>();
      for (Credit credit : creditIterable) {
          if (credit.getLimit() >= desirableAmount) {
              creditList.add(credit);
          }
      }
      if (creditList.size() == 0) {
        Notification.show("Подходящих кредитов не найдено")
            .setPosition(Notification.Position.MIDDLE);
        changeHandler.onChange();
        close();
      } else {
        title.setText("Выберите нужный кредит");
        creditGrid = new Grid<>();
        HorizontalLayout buttons = new HorizontalLayout();
        years = new TextField("Введите срок кредита, лет");
        creditGrid.setItems(creditList);
        creditGrid.addColumn(Credit::getLimit).setHeader("Лимит");
        creditGrid.addColumn(Credit::getPercent).setHeader("%");
        Button acceptButton = new Button("Подтвердить");
        Button cancelButton = new Button("Отмена");
        acceptButton.setEnabled(false);
        buttons.add(acceptButton, cancelButton);
        verticalLayout.add(creditGrid, years, buttons);
        creditGrid.addSelectionListener(e -> {
          acceptButton.setEnabled(true);
        });
        acceptButton.addClickListener(e -> {
          if (years.getValue().trim().matches("[0-9]{1,2}")) {
            createDetailsWindow(creditGrid.asSingleSelect().getValue(), client,
                Integer.parseInt(years.getValue().trim()),
                Double.parseDouble(creditAmount.getValue().trim()));
            creditGrid.setVisible(false);
            years.setVisible(false);
            buttons.setVisible(false);
            add(detailsWindow);
          } else {
            Notification.show("Введите корректный срок кредитования");
          }
        });
        cancelButton.addClickListener(e -> {
          onClose();
          this.close();
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
      Notification.show("Что-то пошло не так.");
      this.close();
      changeHandler.onChange();
    }
  }

  public void createDetailsWindow(Credit credit, Client client, int year, double amount) {
    HorizontalLayout buttonsBar = new HorizontalLayout();
    int months = year * 12;
    double percent = credit.getPercent() / 100;
    double percentScale = percent / 12;
    DecimalFormat df = new DecimalFormat("#.##");
    title.setText("Детали кредита");
    double perMonthPayment = amount *
        (percentScale +
            (percentScale /
                (Math.pow(percentScale + 1, months) - 1)));
    verticalLayout.add(
        new Label("Имя клиента: " + client.getFirstName()),
        new Label("Фамилия: " + client.getLastName()),
        new Label("Паспорт РФ: " + client.getPassportNumber()),
        new Label("Срок кредита: " + year * 12 + " месяцев"),
        new Label("Сумма кредита: " + amount + " руб"),
        new Label("Сумма долга: " + amount * percent + " руб"),
        new Label("Процент: " + credit.getPercent() + " %"),
        new Label("Сумма ежемесячного платежа: " + df.format(perMonthPayment) + " руб")
    );
    Button accept = new Button("Принять");
    accept.addClickListener(e -> {
      saveCreditOffer(client, credit, amount, year);
      onClose();
      close();
    });
    Button decline = new Button("Отменить");
    decline.addClickListener(e -> {
      onClose();
      close();
    });
    buttonsBar.add(accept, decline);
    verticalLayout.add(buttonsBar);

  }

  private void saveCreditOffer(Client client, Credit credit, double amount, int year) {
    try {
      Bank bank = new Bank(credit, client);
      bankRepository.save(bank);
      CreditOffer creditOffer = new CreditOffer(client, credit, amount);
      creditOfferRepo.save(creditOffer);
      double percent = credit.getPercent() / 100.0;
      double percentScale = percent / 12.0;
      int months = year * 12;
      double perMonthPayment = amount *
          (percentScale +
              (percentScale /
                  (Math.pow(percentScale + 1, months) - 1)));
      LocalDateTime localDateTime = LocalDateTime.now();
      double balanceOwed = amount;
      double percentPart = amount * percentScale;
      double debtPart = perMonthPayment - balanceOwed * percentScale;
      Date date = Date.valueOf(localDateTime.toLocalDate());
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
      dateFormat.format(date);

      for (int i = 1; i <= months; i++) {
        Schedule schedule = new Schedule(date, perMonthPayment, debtPart, percentPart,
            client.getId(), credit.getId());
        System.out.println(schedule.toString());
        scheduleRepo.save(schedule);
        balanceOwed = balanceOwed - debtPart;
        debtPart = perMonthPayment - balanceOwed * percentScale;
        percentPart = balanceOwed * percentScale;
        localDateTime = localDateTime.plusMonths(1);
        date = Date.valueOf(localDateTime.toLocalDate());
        dateFormat.format(date);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Notification.show("Что-то пошло не так!.").setPosition(Notification.Position.MIDDLE);
    }

  }

  private void createListeners() {
    accept.addClickListener(e -> {
      firstStage.setVisible(false);
      findCredit();
      changeHandler.onChange();
    });
    cancel.addClickListener(e -> {
      onClose();
      close();
      changeHandler.onChange();
    });
  }

  private void onClose() {
    verticalLayout.removeAll();
    firstStage.setVisible(true);
    creditAmount.setValue("");
    title.setTitle("Подбор кредита");
    verticalLayout.add(title, firstStage);
  }
}

