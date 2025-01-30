import { Link, useLocation, useParams } from "react-router-dom";
import "./Header.css";

const Header: React.FC = () => {
    const { workOperationId } = useParams<{ workOperationId: string }>();
    const location = useLocation();

    // Zjistíme ID z aktuální URL (pokud není v `useParams`)
    const getWorkOperationId = (): string | null => {
        const match = location.pathname.match(/\/(?:schedule|workplaceview|workerview)\/(\d+)/);
        return match ? match[1] : null;
    };

    const id = workOperationId || getWorkOperationId();

    return (
        <header className="header">
            <h1 className="logo">Planování</h1>
            <nav className="nav">
                <Link to="/" className="nav-link">Domů</Link>
                <Link to={id ? `/schedule/${id}` : "/"} className="nav-link">Rozvrh</Link>
                <Link to={id ? `/workplaceview/${id}` : "/"} className="nav-link">Správa</Link>
            </nav>
        </header>
    );
};

export default Header;
