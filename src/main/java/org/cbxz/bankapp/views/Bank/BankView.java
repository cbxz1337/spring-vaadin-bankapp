package org.cbxz.bankapp.views.Bank;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.Setter;
import org.cbxz.bankapp.models.Bank.Bank;
import org.cbxz.bankapp.models.Bank.BankRepository;

import org.cbxz.bankapp.models.creditOffer.CreditOffer;
import org.cbxz.bankapp.models.creditOffer.CreditOfferRepo;
import org.cbxz.bankapp.models.schedule.Schedule;
import org.cbxz.bankapp.models.schedule.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Theme(value = Lumo.class, variant = Lumo.DARK)
@Route("bank")
public class BankView extends VerticalLayout {

    private final BankRepository bankRepository;

    private final CreditOfferRepo creditOfferRepo;

    private final ScheduleRepo scheduleRepo;

    private Grid<Bank> grid = new Grid<>(Bank.class);
    private final TextField filter = new TextField("", "Фильтр");
    private final Button deleteButton = new Button("Удалить");
    private final Button showScheduleDetails = new Button("График платежей");
    private final Button showCreditDetails = new Button("Детали кредита");
    private final Button creditsButton = new Button("Кредиты");
    private final Button clientsButton = new Button("Клиенты");
    private final HorizontalLayout tools = new HorizontalLayout(filter, deleteButton, showCreditDetails, showScheduleDetails);
    private final HorizontalLayout navigationBar = new HorizontalLayout(creditsButton, clientsButton);


    @Setter
    private ChangeHandler changeHandler;


    public interface ChangeHandler {
        void onChange();
    }


    @Autowired
    public BankView(BankRepository bankRepository, CreditOfferRepo creditOfferRepo, ScheduleRepo scheduleRepo) {
        this.creditOfferRepo = creditOfferRepo;
        this.bankRepository = bankRepository;
        this.scheduleRepo = scheduleRepo;
        grid.setHeight("80vh");
        grid.setColumns("client", "credit");
        grid.getColumnByKey("client").setHeader("Клиент");
        grid.getColumnByKey("credit").setHeader("Кредит");
        tools.setSpacing(true);
        showCreditDetails.setIcon(VaadinIcon.OPEN_BOOK.create());
        showCreditDetails.setEnabled(false);
        showScheduleDetails.setEnabled(false);
        deleteButton.setEnabled(false);
        deleteButton.setIcon(VaadinIcon.TRASH.create());
        tools.setJustifyContentMode(JustifyContentMode.START);
        tools.setSizeFull();
        tools.setAlignItems(Alignment.START);
        navigationBar.setSizeFull();
        navigationBar.setJustifyContentMode(JustifyContentMode.CENTER);
        clientsButton.getElement().getThemeList().add("primary");
        clientsButton.setIcon(VaadinIcon.MAGIC.create());
        creditsButton.getElement().getThemeList().add("primary");
        add(tools, grid, navigationBar);
        grid
                .asSingleSelect()
                .addValueChangeListener(AbstractField.ComponentValueChangeEvent::getValue);
        showBanks();
        buttonsListeners();
        setChangeHandler(this::showBanks);

    }

    private void buttonsListeners() {
        grid.addSelectionListener(e -> {
            deleteButton.setEnabled(true);
            showCreditDetails.setEnabled(true);
            showScheduleDetails.setEnabled(true);
        });
        creditsButton.addClickListener(e -> {
            creditsButton.getUI().ifPresent(ui -> {
                ui.navigate("credits");
            });
        });
        clientsButton.addClickListener(e -> {
            clientsButton.getUI().ifPresent(ui -> {
                ui.navigate("");
            });
        });
        showScheduleDetails.addClickListener(e -> {
            scheduleDetails().open();
        });
        deleteButton.addClickListener(e -> {
            deleteBank();
            changeHandler.onChange();
        });
        showCreditDetails.addClickListener(e -> {
            showCreditDetails().open();
        });
    }

    private Dialog scheduleDetails() {
        Dialog dialog = new Dialog();
        dialog.getElement().getThemeList().add("primary");
        dialog.getElement().getStyle()
                .set("border", "solid")
                .set("border-radius", "140px");
        VerticalLayout verticalLayout = new VerticalLayout();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);
        Bank bank = grid.asSingleSelect().getValue();
        try {
            Iterable<Schedule> schedulesIterable = scheduleRepo.findByClientIdAndCreditId(bank.getClient().getId(), bank.getCredit().getId());
            List<Schedule> schedules = new ArrayList<>();
            for (Schedule schedule : schedulesIterable) {
                schedules.add(schedule);
                Label label = new Label();
                label.getElement().getStyle()
                        .set("border", "solid")
                        .set("border-radius", "10px")
                        .set("border-width", "thin")
                        .set("color", "#dce3d8");
                label.setText(schedule.toString());
                verticalLayout.add(label);
            }
            dialog.add(verticalLayout);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Не удалось отобразить данные").setPosition(Notification.Position.MIDDLE);
        }
        return dialog;
    }

    private Dialog showCreditDetails() {
        Dialog dialog = new Dialog();
        Label label = new Label("Детали кредита");
        label.getStyle()
                .set("border", "none")
                .set("border-bottom", "1px solid white")
                .set("font-weight", "bold");
        dialog.setCloseOnOutsideClick(true);
        dialog.setCloseOnEsc(true);
        dialog.getElement().getStyle()
                .set("border", "solid");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setAlignItems(Alignment.CENTER);
        try {
            Bank bank = grid.asSingleSelect().getValue();
            DecimalFormat df = new DecimalFormat("#.##");
            CreditOffer creditOffer = creditOfferRepo.findByCreditAndClient(bank.getCredit(), bank.getClient());
            double sumWithPercent = creditOffer.getSum() * bank.getCredit().getPercent() + creditOffer.getSum();
            Label clientDetailsLabel = new Label(bank.getClient().toString());
            clientDetailsLabel.getStyle()
                    .set("border", "none")
                    .set("border-bottom", "1px white solid");
            clientDetailsLabel.getElement().getThemeList();
            Label creditDetailsLabel = new Label(bank.getCredit().toString() + " сумма с учетом процентов: " + df.format(sumWithPercent));
            creditDetailsLabel.getStyle()
                    .set("border", "none")
                    .set("border-bottom", "1px white solid");
            verticalLayout.add(label, creditDetailsLabel, clientDetailsLabel);
            dialog.add(verticalLayout);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Что-то пошло не так.");
        }
        return dialog;
    }

    private void showBanks() {
        Iterable<Bank> bankIterable = bankRepository.findAll();
        List<Bank> bankList = new ArrayList<>();
        for (Bank bank : bankIterable) {
            bankList.add(bank);
        }
        grid.setItems(bankList);
    }

    private void deleteBank() {
        Bank bank = grid.asSingleSelect().getValue();
        try {
            creditOfferRepo.delete(creditOfferRepo.findByCreditAndClient(bank.getCredit(), bank.getClient()));
            scheduleRepo.deleteAll(scheduleRepo.findByClientIdAndCreditId(bank.getClient().getId(), bank.getCredit().getId()));
            bankRepository.delete(bank);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

