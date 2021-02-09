package org.cbxz.bankapp.views.Client;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.cbxz.bankapp.models.client.Client;
import org.cbxz.bankapp.models.client.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;


@SpringComponent
@UIScope
public class ClientEditor extends Dialog implements KeyNotifier{

    private ClientsRepository clientsRepository;

    private Client client;

    private TextField firstName = new TextField("Имя", "Введите имя");
    private TextField lastName = new TextField("Фамилия", "Введите фамилию");
    private TextField patronymic = new TextField("Отчество", "Введите отчество");
    private TextField email = new TextField("Email", "Введите email");
    private TextField phoneNumber = new TextField("Номер телефона", "Введите номер телефона");
    private TextField passportNumber = new TextField("Номер паспорта", "Введите номер паспорта");

    private final  String phoneRegexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
    private Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Отмена");
    private  HorizontalLayout buttons = new HorizontalLayout(save, cancel);

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
        add(firstName,lastName,patronymic,email,phoneNumber,passportNumber, buttons);
        binder.bindInstanceFields(this);
        binder.forField(phoneNumber)
                .withValidator(new RegexpValidator("Неверный формат номера", phoneRegexp))
                .bind(Client::getPhoneNumber, Client::setPhoneNumber);
        binder.forField(email)
                .withValidator(new EmailValidator("Не верный формат email"))
                .bind(Client::getEmail, Client::setEmail);
        save.getElement().getThemeList().add("primary");
        addKeyPressListener(Key.ENTER, e->save());
        save.addClickListener(e->{
            save();
            this.close();
        });

        cancel.addClickListener(e->editClient(client));
        cancel.addClickListener(e->this.close());
            }

    public void save() {
        Optional<Client> clientOptional = clientsRepository.findByPassportNumber(passportNumber.getValue());
        if(clientOptional.isEmpty()){
            client.setFirstName(firstName.getValue());
            client.setLastName(lastName.getValue());
        }
        clientsRepository.save(client);
        changeHandler.onChange();
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

        lastName.focus();
    }
}

