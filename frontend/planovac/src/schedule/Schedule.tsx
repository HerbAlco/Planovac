import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import './Schedule.css';

interface Workplace {
    id: number;
    name: string;
    workerName: string;
    helper: string;
    attendanceName: string;
    attendanceValue: string;
}

const Schedule: React.FC = () => {
    const { workOperationId } = useParams<{ workOperationId: string }>();
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [workplaces, setWorkplaces] = useState<Workplace[]>([]);

    useEffect(() => {
        if (!workOperationId) return;

        axios.get(`http://localhost:8080/api/workOperation/${workOperationId}`)
            .then((response) => {
                const workOperation = response.data;
                const sortedWorkplaces = workOperation.workplaces.sort((a: Workplace, b: Workplace) => a.id - b.id);
                setWorkplaces(sortedWorkplaces);
            })
            .catch((error) => {
                if (error.name !== "AbortError") {
                    console.error("Error fetching workOperation: ", error);
                    setErrorMessage("Failed to load workplaces.");
                }
            });
    }, [workOperationId]);

    return (
        <div className="container">
            <h1>Rozvrh</h1>
            <Link to={`/workplaceview/${workOperationId}`}>
                Správa pracovičtě
            </Link>
            <table>
                <thead>
                    <tr>
                        <th>Název pracoviště</th>
                        <th>Obsazení</th>
                        <th>Výpomoc</th>
                        <th>Jméno</th>
                        <th>Docházka</th>
                    </tr>
                </thead>
                <tbody>
                    {workplaces.length > 0 ? (
                        workplaces.map((workplace) => (
                            <tr key={workplace.id}>
                                <td>{workplace.name}</td>
                                <td>{workplace.workerName}</td>
                                <td>{workplace.helper}</td>
                                <td>{workplace.attendanceName}</td>
                                <td>{workplace.attendanceValue}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={5}>No workplaces found.</td>
                        </tr>
                    )}
                </tbody>
            </table>
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
        </div>
    );
};

export default Schedule;