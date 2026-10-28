package it.uniroma3.siw.siwcinefestival.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class RegistrationForm {


    @NotBlank(message = "Lo username è obbligatorio.")
    @Size(
        min = 3,
        max = 50,
        message = "Lo username deve contenere tra 3 e 50 caratteri."
    )
    private String username;


    @NotBlank(message = "L'email è obbligatoria.")
    @Email(message = "Inserisci un indirizzo email valido.")
    @Size(max = 255, message = "L'email è troppo lunga.")
    private String email;


    @NotBlank(message = "La password è obbligatoria.")
    @Size(
        min = 8,
        message = "La password deve contenere almeno 8 caratteri."
    )
    private String password;



    public RegistrationForm() {
    }



    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }
}