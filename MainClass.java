
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class MainClass {

    JFrame frame;// okienko
    JTable table;// tabela
    JMenuBar menuBar;
    JMenu menuPlik;
    JMenuItem menuItemOtworz;
    JMenuItem menuItemZapisz;
    JMenuItem menuItemUsunWiersz;
    JMenuItem menuItemNowy;
    boolean loaded = false;//flaga czy dane sa ladowane z pliku

    Vector<Vector<String>> vData = new Vector<Vector<String>>();//wektor z głownymi danymi
    DefaultTableModel model;//nasz model tabeli(zeby nie pisac table.getTableModel()...

    MainClass() //konstruktor okna pustego
    {
       startUp();
    }
    MainClass(Vector<Vector<String>> loadedData) //konstruktor okna z zaladowanymi danymi
    {
        this.loaded = true;
        this.vData = loadedData;
        startUp();
    }

    void startUp()//glowna funkcja klasy
    {
        //inicjalizacja okna
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//zamkniecie po wcisnieciu X, a nie ukrycie

        // nazwa okna
        frame.setTitle("Projekt 4 Karasek 18109");

        // Nazwy kolumn
        Vector<String> startUp = new Vector<String >();
        String[] headerss = new String[]{"Nr albumu","Osoba","Grupa","Praca domowa","Aktywność","Projekt","Kolokwium 1", "Kolokwium 2", "Egzamin", "Suma"};
        startUp.addAll(Arrays.asList(headerss));

        // inicjalizacja tabeli
        table = new JTable();
        table.setBounds(30, 40, 200, 300);

        model = (DefaultTableModel) table.getModel(); //zdobywanie modelu tabeli zeby dodawac wiersze itp..

        for (String s: startUp)//dodawanie nazw kolumn
        {
            model.addColumn(s);
        }
        if(loaded) //dodawanie do tabeli po zaladowaniu pliku
        {
            for (Vector<String> vec : vData) {
                model.addRow(vec);
                model.setValueAt(sum(vData,model.getRowCount()-1),model.getRowCount()-1,9);
            }
        }
        addEmpty();//dodaje pusty wiersz zeby mozna bylo cos wpisywac

        // dodawanie do scrollpane i parametry
        JScrollPane sp = new JScrollPane(table);
        frame.add(sp);
        frame.setSize(1000, 300);
        frame.setVisible(true);
        menuGen();



        model.addTableModelListener(new TableModelListener() //dodanie sluchacza do tabeli (reakcja po zmianie wartosci komorki)
        {
            @Override
            public void tableChanged(TableModelEvent e) {
                System.out.println("Column: " + e.getColumn() + " Row: " + e.getFirstRow());


                //Wartość podana przez użytkownika w komórce tabeli
                String input = (String) model.getValueAt(e.getFirstRow(), e.getColumn());
                input.trim();





                switch (e.getColumn()){
                    case 0: {//nr albumu: sprawdzam czy nie jest pusty, i czy jest złożony z cyfr

                        if (!StringUtils.isEmpty(input) && StringUtils.isNumeric(input)) {


                            if(exists(vData,input))
                            {
                                infoBox("Numer indeksu już istnieje!", "Złe wprowadzenie!");
                                model.removeTableModelListener(this);
                                model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                                model.addTableModelListener(this);
                            }
                            else
                                vData.get(e.getFirstRow()).set(e.getColumn(), input);

                        } else {
                            infoBox("W polu NR ALBUMU musisz podac tylko cyfry!", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);

                        }
                        break;
                    }
                    case 1:{//Osoba: sprawdzam czy nie jest pusty i czy posiada spacje - dwa człony
                        if (!StringUtils.isEmpty(input) && input.contains(" ")) {
                            vData.get(e.getFirstRow()).set(e.getColumn(), input);
                            System.out.print("rekord: " + vData.get(e.getFirstRow()));
                            ;
                        }
                        else {
                            infoBox("W polu OSOBA musisz podac co najmniej dwa człony!", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);

                        }
                        break;
                    }
                    case 2:{//Grupa: sprawdzam czy nie jest pusty
                        if (!StringUtils.isEmpty(input)) {
                            vData.get(e.getFirstRow()).set(e.getColumn(), input);

                            System.out.print("rekord: " + vData.get(e.getFirstRow()));

                        }
                        else {
                            infoBox("Pole GRUPA nie może być puste!", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);

                        }
                        break;
                    }
                    case 3://Praca domowa: sprawdzam czy jest liczba i czy jest w zakresie 0-5
                    case 4: {//Aktywność: sprawdzam czy jest liczba i czy jest w zakresie 0-5
                        if(!StringUtils.isEmpty(input))
                        {
                            model.removeTableModelListener(this);
                            punktowe(input,5,e,model);
                            model.addTableModelListener(this);
                        }
                        else
                        {
                            infoBox("Nie może być puste", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);
                        }
                        break;
                    }//Aktywność: sprawdzam czy jest liczba i czy jest w zakresie 0-5
                    case 5://Projekt: sprawdzam czy jest liczba i czy jest w zakresie 0-10
                    {
                        if(!StringUtils.isEmpty(input))
                        {
                            model.removeTableModelListener(this);
                            punktowe(input,10,e,model);
                            model.addTableModelListener(this);
                        }
                        else
                        {
                            infoBox("Nie może być puste", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);
                        }
                        break;
                    }
                    case 6://Kolokwium1: sprawdzam czy jest liczba i czy jest w zakresie 0-10
                    case 7://Kolokwium2: sprawdzam czy jest liczba i czy jest w zakresie 0-10
                    {
                        if(!StringUtils.isEmpty(input))
                        {
                            model.removeTableModelListener(this);
                            punktowe(input,20,e,model);
                            model.addTableModelListener(this);
                        }
                        else
                        {
                            infoBox("Nie może być puste", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);
                        }
                        break;
                    }
                    case 8://Egzamin: sprawdzam czy jest liczba i czy jest w zakresie 0-10
                    {
                        if(!StringUtils.isEmpty(input))
                        {
                            model.removeTableModelListener(this);
                            punktowe(input,40,e,model);
                            model.addTableModelListener(this);
                        }
                        else
                        {
                            infoBox("Nie może być puste", "Złe wprowadzenie!");
                            model.removeTableModelListener(this);
                            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
                            model.addTableModelListener(this);
                        }
                        break;
                    }
                }

                if(!StringUtils.isEmpty(vData.get(e.getFirstRow()).get(0))&&//sprawdzam czy nie jest pusty zeby wyliczyc sume
                        !StringUtils.isEmpty(vData.get(e.getFirstRow()).get(1))&&
                        !StringUtils.isEmpty(vData.get(e.getFirstRow()).get(2)))
                {
                    if(StringUtils.isEmpty(vData.get(e.getFirstRow()).get(3)))//jak dodane są 3 pierwsze kolumny to wypełnia resztę zerami
                    {
                        model.removeTableModelListener(this);
                            for(int i = 3; i<=8;i++)
                            {
                                vData.get(e.getFirstRow()).set(i,"0");
                                model.setValueAt("0",e.getFirstRow(),i);
                            }
                            model.addTableModelListener(this);
                    }

                    int sum = 0;//zmienna przechowujaca sume
                    for (int i = 3; i<=8;i++) {//petla do liczenia sumy z komorek
                        try{
                            sum+=Integer.parseInt(vData.get(e.getFirstRow()).get(i));

                        }
                        catch (NumberFormatException x)
                        {

                        }
                    }
                    try{//ustawianie sumy w komorce
                        vData.get(e.getFirstRow()).set(9,new String(String.valueOf(sum)));
                        model.removeTableModelListener(this);
                        model.setValueAt(String.valueOf(sum),e.getFirstRow(),9);
                        if (model.getValueAt(model.getRowCount()-1,9)!="")
                            addEmpty();
                        model.addTableModelListener(this);
                    }
                    catch (ClassCastException x)
                    {
                        System.out.println(x);
                    }
                }
            }
        });
    }

    void punktowe(String input, int max,TableModelEvent e, TableModel m)//funkcja sprawdza czy podany lancuch jest z liczb, czy nie jest pusty i czy miesci sie w zakresie kryterium
    {
        if (StringUtils.isNumeric(input)||StringUtils.isEmpty(input))
        {
            if (Integer.parseInt(input) >=0 && Integer.parseInt(input) <=max)
            {
                vData.get(e.getFirstRow()).set(e.getColumn(), input);
                System.out.println("rekord: " + vData.get(e.getFirstRow()));

            }
            else
            {//komunikat i zeruje komorke
                infoBox("Można przyznać od 0 do "+max+ " punktów.", "Złe wprowadzenie!");

                model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());

            }
        }
        else {//jw
            infoBox("Pole musi być liczbą!", "Złe wprowadzenie!");
            model.setValueAt(vData.get(e.getFirstRow()).get(e.getColumn()),e.getFirstRow(),e.getColumn());
        }
    }
    int sum(Vector<Vector<String>>vData, int row)//liczy sume dla danego wiersza
    {
        int sum= 0;
        for (int i = 3; i<=8; i++)
        {
            try{
                sum+= Integer.parseInt(vData.get(row).get(i));
            }
            catch (NumberFormatException e)
            {

            }
        }
        return sum;
    }
    void addEmpty()//dodaje pusty wiersz do tabelki i do vektora z danymi
    {
        this.model.addRow(new String[]{"","","","","","","","","",""});//tabelka

        this.vData.add(new Vector<String>(Arrays.asList(new String[]{"","","","","","","","","","0"})));//dane

    }
    void menuGen()//dodaje plik -> wczytaj/zapisz (ten pasek co mozna go wciskac i wybierac sobie co sie chce)
    {

        ActionListener actionListenerOtworz = new ActionListener() //listener otworz, wywoluje konstruktor klasy z nowymi danymi, zamyka poprzednie okno
        {
            @Override
            public void actionPerformed(ActionEvent e) {

                new MainClass(loadFromFile(textBox("Podaj scieżke pliku do odczytu", "Odczyt pliku")));
                frame.dispose();

            }
        };
        ActionListener actionListenerZapisz = new ActionListener() //action listener zapisuje do pliku
        {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    saveToFile(textBox("Podaj scieżke zapisu do pliku", "Zapis pliku"));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        ActionListener actionListenerUsunWiersz = new ActionListener() //Listener, generuje nowe okno z nowymi danymi, a potem zamyka obecną instancję "frame"
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(vData.size()>=2) //sprawdza czy jest co usunąć...
                {
                    new MainClass(deleteRows(selectBox("Podaj rekord do usuniecia", "Usuwanie rekordu")));
                    frame.dispose();
                }
                else{
                    infoBox("Nie ma czego usuwać","ilość zapisanych rekordów: "+ (vData.size()-1));
                }

            }
        };

        ActionListener actionListenerNowy = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainClass();
                frame.dispose();

            }
        };


        menuBar = new JMenuBar();
        menuPlik = new JMenu("Plik");
        menuBar.add(menuPlik);
        menuItemOtworz = new JMenuItem("Otwórz");
        menuPlik.add(menuItemOtworz);
        menuItemZapisz = new JMenuItem("Zapisz");
        menuPlik.add(menuItemZapisz);
        menuItemNowy = new JMenuItem("Nowy");
        menuPlik.add(menuItemNowy);
        menuItemUsunWiersz = new JMenuItem("Usun wiersz");
        menuBar.add(menuItemUsunWiersz);
        frame.setJMenuBar(menuBar);
        menuItemZapisz.addActionListener(actionListenerZapisz);
        menuItemOtworz.addActionListener(actionListenerOtworz);
        menuItemUsunWiersz.addActionListener(actionListenerUsunWiersz);
        menuItemNowy.addActionListener(actionListenerNowy);

    }
    void infoBox(String infoMessage, String titleBar)//okienko z komunikatem
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Błąd: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    String textBox(String infoMessage, String titleBar)//okienko z komunikatem i do wpisania, zwraca tekst wpisany jako sciezke. Domyslnie wskazuje na C:\
    {

        return (String) JOptionPane.showInputDialog(null, infoMessage, titleBar, 1, null,
                null, "C:\\Stud\\Studenci.txt");
    }
    String selectBox(String infoMessage, String titleBar)//Usuwanie rekordu
    {
        String options[] = new String[vData.size()-1];//tablica do przechowywania możliwych opcji
        options[0] = vData.get(0).get(0);//tak musi byc
        for (int i = 1; i<vData.size()-1; i++)//petla po wektorze vData, dodaje do tablicy string opcje
        {
            options[i]=new String(vData.get(i).get(0));
        }
        return  (String)JOptionPane.showInputDialog(null, "Wybierz indeks studenta do usunięcia rekordu",
                "Usuwanie studenta", JOptionPane.QUESTION_MESSAGE, null, options, options[vData.size()-2]);
    }
    boolean exists(Vector<Vector<String>>vData,String input)
    {
        for (Vector<String>s : vData)
        {
            if(s.contains(input))
                return true;
        }
        return false;
    }
    Vector<Vector<String>> loadFromFile(String path) //odczyt z pliku
    {
        File plik = new File(path); //plik
        Vector<Vector<String>> ret = new Vector<Vector<String>>();//wektor do zwrotu (bedzie przyjety przez konstruktor
        Vector<String>line= new Vector<String>();           //wektor przechowujacy linie
        try
        {
            Scanner scanner = new Scanner(plik);            //scanner do czytania z naszego pliku
            while (scanner.hasNextLine()) {                 //dopoki czyta linie
                String data = scanner.nextLine();           //odczyt

                line.addAll(Arrays.asList(data.split(",",-1))); //dodaje tablice zrobiona z lancucha znakow podzielona przez split
                ret.add(new Vector<String>(line));                          //dodanie do wektora ktory bedzie zwracany
                line.clear();                                               //czyszczenie bufora
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            textBox("Nie ma takiego pliku", "Brak pliku");
        }

        ret.remove(ret.size()-1);//usuwanie ostatniego rekordu ( są tam same "")
        return ret;
    }
    void saveToFile(String path) throws IOException //Zapis do pliku
    {
        File plik = new File(path);
       plik.mkdirs();//robi foldery zeby bylo gdzie zrobic plik
            if(!plik.createNewFile())//jezeli plik istnieje to usuwam i robie nowy
            {
                plik.delete();
                plik.createNewFile();
            }
        String line = new String();//zmienna przechowywująca linie zeby ją potem zapisac
        try
        {
            PrintWriter printWriter = new PrintWriter(path);
            for (Vector<String>s:vData)//petla po pierwszym wektorze
            {
                line=s.get(0);//pierwszy element dopisuje
                for(int i = 1; i<=s.size()-2; i++){//petla po reszcie - ostatni
                    line+= ",";//separator
                    line += s.get(i);//kolejny element
                }
                printWriter.println(line);//wpisuje linie do pliku
            }
            printWriter.close();//zamyka, zapisuje
        }
        catch (FileNotFoundException e)
        {
            infoBox("Plik nie istnieje", "");

        }
    }
    Vector<Vector<String>> deleteRows(String row)//zwraca wektor dla nowego konstruktora po usunieciu rekordu
    {
        Vector<Vector<String>> ret = new Vector<Vector<String>>();//wyjsciowy wektor
        try
        {
            for(Vector<String>s:vData)  //petla po elementach vData
            {
                if(!s.contains(row))    //jezeli zawiera dany wiersz to nie rob
                ret.add(s);//dodaj do zwracanego wektora
            }
        }
        catch(ArrayIndexOutOfBoundsException e)
        {

        }
        ret.remove(ret.size()-1);//usuwa rekord pusty
        return ret;
    }
    public static void main(String[] args) {
        new MainClass();
    }
}






