package org.cbxz.bankapp.views.Client;



import com.vaadin.data.Binder;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Setter;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.credit.CreditRepository;
import org.cbxz.bankapp.models.creditOffer.CreditOffer;
import org.cbxz.bankapp.models.creditOffer.CreditOfferRepo;
import org.cbxz.bankapp.models.creditOffer.CreditService;
import org.cbxz.bankapp.models.schedule.Schedule;
import org.cbxz.bankapp.models.schedule.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringComponent
@UIScope
public class CreditOfferEditor extends Dialog implements KeyNotifier{

    private VerticalLayout mainLayout = new VerticalLayout();

    private CreditOfferRepo creditOfferRepo;

    private CreditService creditService;

    private ScheduleRepo scheduleRepo;

    private Client client;

    private TextField creditAmount = new TextField();

    private HorizontalLayout firstStage = new HorizontalLayout();

    private Button accept = new Button("Подтвердить", VaadinIcon.CHECK.create());

    private Button cancel = new Button("Отмена");

    @Setter
    private ChangeHandler changeHandler;


    public interface ChangeHandler{
        void onChange();
    }


    @Autowired
    public CreditOfferEditor(CreditOfferRepo creditOfferRepo, CreditService creditService, ScheduleRepo scheduleRepo){
        this.creditService = creditService;
        this.creditOfferRepo = creditOfferRepo;
        this.scheduleRepo = scheduleRepo;
        creditAmount.setPlaceholder("Введите сумму");
        firstStage.add(creditAmount, accept, cancel);
        firstStage.setSizeFull();
        add(firstStage);
        createListeners();
    }

    private void createListeners(){
        accept.addClickListener(e->{
            firstStage.setVisible(false);
            findCredit();
        });
        cancel.addClickListener(e->{
            removeAll();
            close();
        });
    }

    public void findCredit(){
        long desirableAmount = Long.parseLong(creditAmount.getValue());
        Iterable<Credit> creditIterable = creditService.findAll();
        List<Credit> creditList = new ArrayList<>();
        for(Credit credit : creditIterable){
            if(credit.getLimit()>=desirableAmount)
            creditList.add(credit);
        }
        if (creditList.size()==0){
            System.out.println(creditList.size());
            add(new Label("asd"));
        }
        else {
            Label chooseLabel = new Label("Выберите нужный кредит");
            Grid<Credit> creditGrid = new Grid<>();
            HorizontalLayout buttons = new HorizontalLayout();
            TextField years = new TextField("Введите срок кредита");
            creditGrid.setItems(creditList);
            creditGrid.addColumn(Credit::getLimit).setHeader("Лимит");
            creditGrid.addColumn(Credit::getPercent).setHeader("%");
            Button acceptButton = new Button("Подтвердить");
            Button cancelButton = new Button("Отмена");
            acceptButton.setEnabled(false);
            buttons.add(acceptButton, cancelButton);
            add(chooseLabel, creditGrid, years, buttons);
            creditGrid.addSelectionListener(e->{
                acceptButton.setEnabled(true);
            });
            acceptButton.addClickListener(e->{ calculateOffer(client, Integer.parseInt(years.getValue().trim()),
                    creditGrid.asSingleSelect().getValue(),
                    Long.parseLong(creditAmount.getValue().trim()));
            });
        }
    }
    public void setClient(Client client){
        this.client = client;
    }

    public CreditOffer calculateOffer(Client client, int year, Credit credit, long amount){
        List<Schedule> schedules = new ArrayList<>();
        double percent = credit.getPercent()/100;
        int months = year/12;
        double perMonthPayment = amount * (percent + (percent/(Math.pow(percent+1,months)-1)));
        double percentPart = amount*percent;
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.valueOf(localDateTime.toLocalDate());
        for(int i = 1; i<=year; i++){
            long balanceOwed = amount;
            Schedule schedule = new Schedule(date, perMonthPayment, balanceOwed, percentPart);
            schedules.add(schedule);
            balanceOwed-=perMonthPayment;
            percentPart = balanceOwed*percent;
            localDateTime = localDateTime.plusMonths(1);
            date = Date.valueOf(localDateTime.toLocalDate());
        }
        System.out.println("УГАБУКА");
        CreditOffer creditOffer = new CreditOffer(client, credit,(double)amount,schedules);
        scheduleRepo.saveAll(schedules);
        creditOfferRepo.save(creditOffer);
        return creditOffer;
    }

}

