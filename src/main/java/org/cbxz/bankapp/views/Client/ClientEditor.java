package org.cbxz.bankapp.views.Client;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.shared.Position;
import lombok.Setter;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.client.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SpringComponent
@UIScope
public class ClientEditor extends Dialog implements KeyNotifier{

    private ClientsRepository clientsRepository;

    private Client client;

    private final TextField firstName = new TextField("Имя", "Введите имя");
    private final TextField lastName = new TextField("Фамилия", "Введите фамилию");
    private final TextField patronymic = new TextField("Отчество", "Введите отчество");
    private final TextField email = new TextField("Email", "Введите email");
    private final TextField phoneNumber = new TextField("Номер телефона", "Введите номер телефона");
    private final TextField passportNumber = new TextField("Номер паспорта", "Введите номер паспорта");

    private final  String phoneRegexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
    private Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Отмена");
    private  HorizontalLayout buttons = new HorizontalLayout(save, cancel);
    private VerticalLayout mainLayout = new VerticalLayout();
    private HorizontalLayout nameBar = new HorizontalLayout();

    private Binder<Client> binder = new Binder<>(Client.class);

    @Setter
    private ChangeHandler changeHandler;


    public interface ChangeHandler{
        void onChange();
    }


    @Autowired
    public ClientEditor(ClientsRepository clientsRepository){
        setCloseOnOutsideClick(true);
        this.clientsRepository = clientsRepository;
        firstName.setRequired(true);
        lastName.setRequired(true);
        passportNumber.setRequired(true);
        phoneNumber.setRequired(true);
        nameBar.add(firstName, lastName);
        mainLayout.add(nameBar,patronymic,email,phoneNumber,passportNumber, buttons);
        add(mainLayout);
        binder.bindInstanceFields(this);
        binder.forField(phoneNumber)
                .withValidator(new RegexpValidator("Неверный формат номера", phoneRegexp))
                .bind(Client::getPhoneNumber, Client::setPhoneNumber);
        binder.forField(email)
                .withValidator(new EmailValidator("Неверный формат email"))
                .bind(Client::getEmail, Client::setEmail);
        save.getElement().getThemeList().add("primary");
        save.addClickListener(e->{
            if(fieldValidator()){
                save();
                this.close();
            }
            else {
               Notification.show("Вы не заполнили все необходимые поля").setPosition(Notification.Position.MIDDLE);
            }
        });

        cancel.addClickListener(e->editClient(client));
        cancel.addClickListener(e->this.close());
    }

    public void save() {
        try {
            Optional<Client> clientOptional = clientsRepository.findByPassportNumber(passportNumber.getValue());
            if(clientOptional.isEmpty()){
                clientsRepository.save(client);
            }
            else {
                Notification.show("Клиент с таким паспортом уже сущестует.").setPosition(Notification.Position.MIDDLE);
            }
            changeHandler.onChange();
        }
        catch (Exception e){
            e.printStackTrace();
            Notification.show("Что-то пошло не так.");
        }

    }

    private boolean fieldValidator(){
        return firstNameValidator()
                &&lastNameValidator()
                &&phoneNumberValidator()
                &&passportNumberValidator();
    }

    private boolean firstNameValidator(){
       return firstName.getValue().trim().matches("^[А-Я][а-я]{2,15}");
    }

    private boolean lastNameValidator(){
        return lastName.getValue().trim().matches("^[А-Я][а-я]{2,15}");
    }

    private boolean passportNumberValidator(){
        return passportNumber.getValue().trim().matches("[\\d]{10}");
    }

    private boolean phoneNumberValidator(){
        return passportNumber.getValue().trim().matches(phoneRegexp);
    }

    public void editClient(Client newClient){
        if(newClient == null){
            setVisible(false);
            return;
        }
        if (newClient.getId()!=null){
            this.client = clientsRepository.findById(newClient.getId()).orElse(newClient);
        }
        else {
            this.client = newClient;
        }
        binder.setBean(this.client);
        setVisible(true);
        firstName.focus();
    }
}

