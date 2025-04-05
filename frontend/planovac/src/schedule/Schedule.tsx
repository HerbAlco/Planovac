import axios from "axios";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "./Schedule.css";
import AbsenceLegend from "./AbsenceLegend";

interface Workplace {
    id: number;
    name: string;
    helper?: string;
}

interface Worker {
    id: number;
    name: string;
}

interface ScheduleAssignment {
    workplace: Workplace;
    workers: Worker[];
    date: Date;
}

interface ScheduleData {
    scheduleAssignments: ScheduleAssignment[];
}

enum AbsenceTypeEnum {
    Z = "Z volno",
    D = "Dovolená",
    NV = "Náhradní volno",
    PG = "Paragraf",
    PLP = "Preventivní prohlídka",
    N = "Nemoc",
    ST = "Studijní volno",
    OCR = "Ošetřovné",
    SK = "Školení",
    SV = "Sváteční volno",
    SH = "Stará hala",
}

const Schedule: React.FC = () => {
    const { workOperationId } = useParams<{ workOperationId: string }>();
    const [errorMessage, setErrorMessage] = useState<string>("");
    const [workplaces, setWorkplaces] = useState<Workplace[]>([]);
    const [workers, setWorkers] = useState<Worker[]>([]);
    const [scheduleData, setScheduleData] = useState<ScheduleData | null>(null);
    const [selectedAbsences, setSelectedAbsences] = useState<{ [key: number]: string }>({});

    useEffect(() => {
        if (!workOperationId) return;

        axios
            .get(`http://localhost:8080/api/workOperation/${workOperationId}`)
            .then((response) => {
                const workOperationData = response.data;

                setWorkplaces(workOperationData.workplaces);

                // Seřazení pracovníků podle jména (kopie pole, aby nedošlo k mutaci)
                const sortedWorkers = [...workOperationData.workers].sort((a, b) =>
                    a.name.localeCompare(b.name)
                );
                setWorkers(sortedWorkers);
            })
            .catch((error) => {
                console.error("Error fetching workOperation: ", error);
                setErrorMessage("Failed to load workplaces.");
            });

        axios
            .get(`http://localhost:8080/api/schedule/${workOperationId}`)
            .then((response) => {
                setScheduleData(response.data);
            })
            .catch((error) => {
                console.error("Error fetching schedule: ", error);
                setErrorMessage("Failed to load schedule.");
            });
    }, [workOperationId]);

    const handleAbsenceChange = (workerId: number, absenceType: string) => {
        setSelectedAbsences((prev) => ({
            ...prev,
            [workerId]: absenceType,
        }));
    };

    const maxRows = Math.max(workplaces.length, workers.length);

    return (
        <div className="container" style={{ display: "flex", flexDirection: "row", justifyContent: "center", alignContent: "top" }}>
            <div>
                <h1>Rozvrh</h1>
                <Link to={`/workplaceview/${workOperationId}`}>Správa pracoviště</Link>
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
                        {Array.from({ length: maxRows }).map((_, index) => {
                            const workplace = workplaces[index] ?? null;
                            const worker = workers[index] ?? null;
                            const assignment = scheduleData?.scheduleAssignments?.[index] as ScheduleAssignment | undefined;
                            const assignedWorkers = assignment?.workers ?? [];

                            return (
                                <tr key={index}>
                                    <td>{workplace?.name ?? ""}</td>
                                    <td>
                                        {assignedWorkers.length > 0
                                            ? assignedWorkers.map((w) => w.name).join(", ")
                                            : ""}
                                    </td>
                                    <td>{workplace?.helper ?? ""}</td>
                                    <td>{worker?.name ?? ""}</td>
                                    <td>
                                        <select
                                            value={worker ? selectedAbsences[worker.id] ?? "" : ""}
                                            onChange={(e) => worker && handleAbsenceChange(worker.id, e.target.value)}
                                            disabled={!worker}
                                        >
                                            <option value="">Vyber absenci</option>
                                            {Object.entries(AbsenceTypeEnum).map(([key, value]) => (
                                                <option key={key} value={key}>
                                                    {value}
                                                </option>
                                            ))}
                                        </select>
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
                {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
            </div>
            <AbsenceLegend />
        </div>
    );
};

export default Schedule;
