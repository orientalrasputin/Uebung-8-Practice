package covidtracer.kontaktliste;

import covidtracer.stereotypes.Mutable;

import java.util.Objects;

public class Kontaktperson {
  @Mutable
  public String nachname;
  @Mutable
  public String vorname;
  @Mutable
  public String kontaktinformationen;

  public Kontaktperson(String nachname, String vorname, String kontaktinformationen) {
    this.nachname = nachname;
    this.vorname = vorname;
    this.kontaktinformationen = kontaktinformationen;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    Kontaktperson that = (Kontaktperson) o;
    return Objects.equals(nachname, that.nachname) &&
        Objects.equals(vorname, that.vorname) &&
        Objects.equals(kontaktinformationen, that.kontaktinformationen);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nachname, vorname, kontaktinformationen);
  }
}
