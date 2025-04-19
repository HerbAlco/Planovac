import { Button } from 'primereact/button';
import "./HomePage.scss";
import 'primereact/resources/themes/lara-light-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';

export default function HomePage() {
    return (
        <div className="home">
            <header className="header">
                <h1>WorkMatch</h1>
                <p>Chytré přiřazení pracovníků k pracovištím podle priorit</p>
            </header>

            <section className="hero">
                <h2>Optimalizuj pracovní sílu s chytrým algoritmem</h2>
                <p>
                    Naše platforma ti pomůže efektivně přiřadit zaměstnance tam, kde jsou nejefektivnější (i když zastupují).
                    Rychle, spolehlivě a podle zadaných priorit.
                </p>
                <Button
                    label="Spustit aplikaci"
                    icon="pi pi-arrow-right"
                    className="p-button-rounded p-button-lg p-button-primary"
                    onClick={() => window.location.href = '/main'}
                />
            </section>

            <section className="steps">
                <h3>Jak to funguje</h3>
                <div className="stepList">
                    {[
                        {
                            title: '1. Vytvoř pracovní provoz, závod nebo halu',
                            desc: 'Vytvoření pracovního provozu, závodu nebo haly, pro soubor pracovišť.',
                        },
                        {
                            title: '2. Vytvoření pracovišť, pracovníků a jejich priorit',
                            desc: '',
                        },
                        {
                            title: '3. Rozvrh pracovníků pro vybraný den',
                            desc: 'Zobrazení rozdělení pracovníků pro dany den.',
                        },
                    ].map((step, i) => (
                        <div key={i} className="stepCard">
                            <span className="pi pi-cog" />
                            <h4>{step.title}</h4>
                            <p>{step.desc}</p>
                        </div>
                    ))}
                </div>
            </section>

            <section className="benefits">
                <h3>Proč právě WorkMatch?</h3>
                <ul>
                    <li>✅ Automatické přiřazení dle priorit a dovedností</li>
                    <li>✅ Snížení chyb a úspora času</li>
                    <li>✅ Odstranění subjektivních zásahů do plánování</li>
                    <li>✅ Snadné použití bez technických znalostí</li>
                    <li>✅ Připraveno na malé i velké týmy</li>
                </ul>
            </section>

            <footer className="footer">
                <small>&copy; 2025 WorkMatch. Všechna práva vyhrazena.</small>
            </footer>
        </div>
    );
}
