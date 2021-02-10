package org.cbxz.bankapp.views.Credit;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import lombok.Setter;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.credit.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Route("credits")
public class CreditView extends VerticalLayout{

    private CreditRepository creditRepository;

    private Grid<Credit> grid = new Grid<>();
    private final TextField filter = new TextField("", "Фильтр");
    private final Button addNewBtn = new Button("Добавить кредит");
    private final Button editСreditButton = new Button("Изменить");
    private final Button deleteButton = new Button("Удалить");
    private final Button clientsButton = new Button("Клиенты");
    private final Button creditsButton = new Button("Кредиты");
    private final Button banksButton = new Button("Банк");
    private final HorizontalLayout tools = new HorizontalLayout(filter, addNewBtn, editСreditButton, deleteButton);
    private final HorizontalLayout navigationBar = new HorizontalLayout(creditsButton, banksButton, clientsButton);
    private final CreditEditor creditEditor;


    @Setter
    private ChangeHandler changeHandler;


    public interface ChangeHandler{
        void onChange();
    }


    @Autowired
    public CreditView(CreditRepository creditRepository, CreditEditor creditEditor){
        this.creditRepository = creditRepository;
        this.creditEditor = creditEditor;
        grid.setHeight("80vh");
        grid.addColumn(Credit::getId).setHeader("ID");
        grid.addColumn(Credit::getLimit).setHeader("Лимит");
        grid.addColumn(Credit::getPercent).setHeader("%");
        tools.setSpacing(true);
        addNewBtn.setIcon(VaadinIcon.PLUS.create());
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
        filter.addValueChangeListener(e->showClient(e.getValue()));
        add(tools, grid, navigationBar, creditEditor);
        grid
                .asSingleSelect()
                .addValueChangeListener(AbstractField.ComponentValueChangeEvent::getValue);
        creditEditor.setChangeHandler(()->{
            showClient("");
        });
        setChangeHandler(()->{
            showClient("");
        });
        showClient("");
        buttonsListeners();

    }
    private void buttonsListeners(){
        grid.addSelectionListener(e->{
            if(!grid.asSingleSelect().isEmpty()){
                editСreditButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
            else {
                editСreditButton.setEnabled(false);
            }
        });
        addNewBtn.addClickListener(e->{
            creditEditor.open();
            creditEditor.editCredit(new Credit());
        });
        editСreditButton.addClickListener(e->{
            creditEditor.open();
            creditEditor.editCredit(grid.asSingleSelect().getValue());
        });
        deleteButton.addClickListener(e->{
            deleteClient(grid.asSingleSelect().getValue());
            changeHandler.onChange();
            deleteButton.setEnabled(false);
        });
        clientsButton.addClickListener(e->{
            clientsButton.getUI().ifPresent(ui->{
                ui.navigate("");
            });
        });
        banksButton.addClickListener(e->{
            banksButton.getUI().ifPresent(ui->{
                ui.navigate("bank");
            });
        });
    }
    private void deleteClient(Credit credit){
        creditRepository.delete(credit);
    }

    private void showClient(String limit){
        Iterable<Credit> clientIterable = creditRepository.findAll();
        List<Credit> creditList = new ArrayList<>();
        if (limit.isEmpty()){
            for (Credit credit : clientIterable){
                creditList.add(credit);
            }
            grid.setItems(creditList);
        }
    }
}
