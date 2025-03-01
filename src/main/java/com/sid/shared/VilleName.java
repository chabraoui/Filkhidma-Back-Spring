package com.sid.shared;

public enum VilleName {
    AGADIR("Agadir"),
    AL_HOCEIMA("Al Hoceima"),
    AZILAL("Azilal"),
    BENI_MELLAL("Béni Mellal"),
    CASA_BLANCA("Casablanca"),
    CHAOUIA("Chaouia"),
    EL_JADIDA("El Jadida"),
    FES("Fès"),
    GUELMIM("Guelmim"),
    IFRANE("Ifrane"),
    KHENIFRA("Khenifra"),
    MARRAKECH("Marrakech"),
    MEKNES("Meknès"),
    NADOR("Nador"),
    OUJDA("Oujda"),
    RABAT("Rabat"),
    SAFI("Safi"),
    SALE("Salé"),
    TANGER("Tanger"),
    TETOUAN("Tétouan"),
    MICHLIFEN("Michlifene"),
    FES_MEKNES("Fès-Meknès"),
    CASABLANCA_SETAT("Casablanca-Settat"),
    MARRAKECH_SAFI("Marrakech-Safi"),
    DRÂA_TAFILALET("Drâa-Tafilalet"),
    LARACHE("Larache"),
    EL_KHOMRIYAT("El Khomriyat"),
    TATA("Tata"),
    TINGHIR("Tinghir"),
    TINJDAD("Tinjad"),
    TAZA("Taza"),
    SKHIRAT("Skhirat"),
    JERRADA("Jerrada"),
    OUEZZANE("Ouezzane"),
    KENITRA("Kenitra"),
    ERRAJAA("Errajaa"),
    TEMARA("Témara"),
    FIKK("Fikk"),
    IMILCHIL("Imilchil"),
    TAROUDANT("Taroudant"),
    IMLIL("Imlil"),
    GUERCIF("Guercif"),
    MARAISSAT("Maraissat"),
    YOUSSEFIA("Youssoufia"),
    EL_BOUZID("El Bouzid"),
    OUALIDIA("Oualidia"),
    SETTAT("Settat"),
    RABAT_SALÉ_KENITRA("Rabat-Salé-Kénitra"),
    ESSAOUIRA("Essaouira"),
    KHEMISSET("Khemisset"),
    TAOURIRT("Taourirt"),
    GHRISS("Ghriss"),
    MARRAKECH_KHEMISSAT("Marrakech-Khemisset"),
    TAFRAOUT("Tafraout"),
    BENI_MELLAL_KHENIFRA("Béni Mellal-Khénifra"),
    SOUISSI("Souissi"),
    BOUJDOUR("Boujdour"),
    JARDAK("Jardak"),
    ERRACHIDIA("Errachidia"),
    SIDI_IFNI("Sidi Ifni"),
    MIDELT("Midelt"),
    TAMMOULELT("Tammoulet"),
    BERKANE("Berkane");

    private final String villeName;

    VilleName(String villeName) {
        this.villeName = villeName;
    }

    public String getVilleName() {
        return villeName;
    }

    @Override
    public String toString() {
        return this.villeName;
    }
}
