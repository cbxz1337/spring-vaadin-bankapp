package org.cbxz.bankapp.views.Credit;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.cbxz.bankapp.models.credit.Credit;
import org.cbxz.bankapp.models.credit.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;


@SpringComponent
@UIScope
public class CreditEditor extends Dialog implements KeyNotifier {

    private CreditRepository creditRepository;

    private Credit credit;

    private TextField limit = new TextField("Лимит", "Введите лимит");
    private TextField percent = new TextField("Процент %", "Введите процент");
    private Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Отмена");
    private HorizontalLayout buttons = new HorizontalLayout(save, cancel);
    private HorizontalLayout fields = new HorizontalLayout(limit, percent);

    private Binder<Credit> binder = new Binder<>(Credit.class);

    @Setter
    private ChangeHandler changeHandler;


    public interface ChangeHandler{
        void onChange();
    }


    @Autowired
    public CreditEditor(CreditRepository creditRepository){
        setCloseOnOutsideClick(true);
        VerticalLayout layout = new VerticalLayout(fields, buttons);
        this.creditRepository = creditRepository;
        add(layout);
        binder.setBean(credit);
        binder.forField(limit)
                .withConverter(new StringToLongConverter(""))
                .bind(Credit::getLimit, Credit::setLimit);
        binder.forField(percent)
                .withConverter(new StringToDoubleConverter(""))
                .bind(Credit::getPercent, Credit::setPercent);
        binder.bindInstanceFields(this);
        save.getElement().getThemeList().add("primary");
        addKeyPressListener(Key.ENTER, e->save());
        save.addClickListener(e->{
            save();
            this.close();
        });

        cancel.addClickListener(e->editCredit(credit));
        cancel.addClickListener(e->this.close());
    }

    public void save() {
        Optional<Credit> creditOptional = creditRepository.findByLimitAndPercent(credit.getLimit(), credit.getPercent());
        if(creditOptional.isEmpty()){
            credit.setPercent(Double.parseDouble(percent.getValue()));
            credit.setLimit(Long.parseLong(limit.getValue()));
        }
        creditRepository.save(credit);
        changeHandler.onChange();
    }

    public void editCredit(Credit newCredit){
        if(newCredit == null){
            setVisible(false);
            return;
        }
        if (newCredit.getId()!=null){
            this.credit = creditRepository.findByLimitAndPercent(Long.parseLong(limit.getValue()), Double.parseDouble(percent.getValue())).orElse(newCredit);
        }
        else {
            this.credit = newCredit;
        }
        binder.setBean(this.credit);

        setVisible(true);

        limit.focus();
    }
}
