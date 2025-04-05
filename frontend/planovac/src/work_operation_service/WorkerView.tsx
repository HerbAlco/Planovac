import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useParams } from "react-router-dom";
import "./WorkerView.css";
import { toast, ToastContainer } from "react-toastify";

interface Worker {
    id: number;
    name: string;
    available: boolean;
    priorities: {
        workplaceId: number;
        priority: number;
    }[];
}

interface Workplace {
    id: number;
    name: string;
    maxWorkers: number;
}

const WorkerView: React.FC = () => {
    const { workOperationId } = useParams<{ workOperationId: string }>();
    const [workers, setWorkers] = useState<Worker[]>([]);
    const [workplaces, setWorkplaces] = useState<Workplace[]>([]);
    const [loading, setLoading] = useState(true);
    const [editingWorker, setEditingWorker] = useState<{ [key: number]: string }>({});
    const [newWorkerName, setNewWorkerName] = useState("");
    const [newAvailable, setNewAvailable] = useState(true);
    const [showPriorityModal, setShowPriorityModal] = useState(false);
    const [selectedWorkerId, setSelectedWorkerId] = useState<number | null>(null);
    const [selectedWorkplaceId, setSelectedWorkplaceId] = useState<number | null>(null);
    const [priority, setPriority] = useState(1);

    useEffect(() => {
        if (!workOperationId) return;
        fetchWorkers();
        fetchWorkplaces();
    }, [workOperationId]);

    const fetchWorkers = async () => {
        try {
            const response = await axios.get<Worker[]>(
                `http://localhost:8080/api/workOperation/workersfromworkoperation/${workOperationId}`
            );
            setWorkers(response.data.sort((a, b) => a.name.localeCompare(b.name)));
        } catch (err) {
            showMessage("❌ Chyba při získávání pracovníků.", true);
        } finally {
            setLoading(false);
        }
    };

    const fetchWorkplaces = async () => {
        try {
            const response = await axios.get<Workplace[]>(
                `http://localhost:8080/api/workOperation/workplacesfromworkoperation/${workOperationId}`
            );
            setWorkplaces(response.data.sort((a, b) => a.id - b.id));

        } catch (err) {
            showMessage("❌ Chyba při získávání pracovišť.", true);
        } finally {
            setLoading(false);
        }
    };

    const showMessage = (message: string, isError: boolean) => {
        if (isError) {
            toast.error(message);
        } else {
            toast.success(message);
        }
    };

    const handleAddWorker = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!workOperationId) return;

        const newWorker = {
            name: newWorkerName,
            available: newAvailable,
            priorities: [],
        };

        try {
            const response = await axios.post(
                `http://localhost:8080/api/workers/create/${workOperationId}`,
                newWorker
            );
            setWorkers((prevWorkers) => [...prevWorkers, response.data]);
            setNewWorkerName("");
            setNewAvailable(true);
            showMessage("✅ Pracovník úspěšně přidán.", false);
        } catch (err) {
            showMessage("❌ Chyba při přidání pracovníka.", true);
        }
    };

    const handleEditWorker = async (workerId: number) => {
        const updatedName = editingWorker[workerId] || workers.find((worker) => worker.id === workerId)?.name;

        if (!updatedName) return;

        try {
            await axios.put(`http://localhost:8080/api/workers/${workerId}`, { name: updatedName });

            setWorkers((prevWorkers) =>
                prevWorkers.map((worker) => (worker.id === workerId ? { ...worker, name: updatedName } : worker))
            );

            setEditingWorker((prevEditing) => {
                const newEditing = { ...prevEditing };
                delete newEditing[workerId];
                return newEditing;
            });

            showMessage("✅ Pracovník úspěšně upraven.", false);
        } catch (err) {
            showMessage("❌ Chyba při upravě pracovníka.", true);
        }
    };

    const handleDeleteWorker = async (workerId: number) => {
        try {
            await axios.delete(`http://localhost:8080/api/workers/${workerId}`);
            setWorkers((prevWorkers) => prevWorkers.filter((worker) => worker.id !== workerId));
            showMessage("✅ Pracovník úspěšně odstraněn.", false);
        } catch (err) {
            showMessage("❌ Chyba při odstranění pracovníka.", true);
        }
    };

    const handleDeletePriorityClick = async (workerId: number, workplaceId: number) => {
        try {
            await axios.delete(`http://localhost:8080/api/workers/delete-priority/${workerId}`, {
                data: { workplaceId },
                headers: { "Content-Type": "application/json" }
            });

            setWorkers((prevWorkers) =>
                prevWorkers.map((worker) =>
                    worker.id === workerId
                        ? { ...worker, priorities: worker.priorities.filter(p => p.workplaceId !== workplaceId) }
                        : worker
                )
            );

            showMessage("✅ Priorita úspěšně odstraněna.", false);
        } catch (err) {
            showMessage("❌ Chyba při odstranění priority.", true);
        }
    };


    const handlePriorityClick = (workerId: number) => {
        setSelectedWorkerId(workerId);
        setShowPriorityModal(true);
    };

    const handleModalSubmit = async () => {
        if (selectedWorkerId !== null && selectedWorkplaceId !== null) {
            try {
                const response = await axios.post(
                    `http://localhost:8080/api/workers/add-priority/${selectedWorkerId}`,
                    {
                        workplaceId: selectedWorkplaceId,
                        priority: priority,
                    }
                );
                const updatedWorker = response.data;
                const updatedWorkers = workers.map((worker) =>
                    worker.id === selectedWorkerId ? updatedWorker : worker
                );
                setWorkers(updatedWorkers);

                showMessage("✅ Priorita úspěšně přidána.", false);
            } catch (error) {
                console.error("Chyba při ukládání priority:", error);
                showMessage("❌ Chyba při ukládání priority.", true);
            }
        }

        setShowPriorityModal(false);
    };


    const handleModalCancel = () => {
        setShowPriorityModal(false);
    };

    return (
        <div className="worker-container">
            <nav className="worker-nav">
                <Link to={`/workplaceview/${workOperationId}`}>Správa pracovišť</Link>
            </nav>
            <h2>Správa pracovníků</h2>
            <ToastContainer />
            <div className="worker-layout">
                <div className="worker-table-container">
                    {loading ? (
                        <p>Načítání...</p>
                    ) : (
                        <table className="worker-table">
                            <thead>
                                <tr>
                                    <th>Jméno</th>
                                    <th>Pracoviště</th>
                                    <th>Akce</th>
                                </tr>
                            </thead>
                            <tbody>
                                {workers.length > 0 ? (
                                    workers.map((worker) => (
                                        <tr key={worker.id}>
                                            <td className="name">
                                                <input
                                                    type="text"
                                                    value={editingWorker[worker.id] ?? worker.name}
                                                    onChange={(e) =>
                                                        setEditingWorker((prevEditing) => ({
                                                            ...prevEditing,
                                                            [worker.id]: e.target.value,
                                                        }))
                                                    }
                                                />
                                            </td>
                                            <td className="priorities">
                                                {worker.priorities.length > 0 ? (
                                                    <ul>
                                                        {worker.priorities.map((priority) => {
                                                            const workplace = workplaces.find((wp) => wp.id === priority.workplaceId);
                                                            return (
                                                                <li key={priority.workplaceId}>
                                                                    {workplace ? workplace.name : "Neznámé pracoviště"} - {priority.priority}
                                                                    <button
                                                                        onClick={() => handleDeletePriorityClick(worker.id, priority.workplaceId)}
                                                                        style={{ marginLeft: "10px", color: "red" }}
                                                                    >
                                                                        ❌
                                                                    </button>
                                                                </li>
                                                            );
                                                        })}
                                                    </ul>
                                                ) : (
                                                    "Žádné priority"
                                                )}
                                            </td>

                                            <td className="actions">
                                                <button onClick={() => handleEditWorker(worker.id)} className="edit-button">
                                                    Upravit
                                                </button>
                                                <button onClick={() => handleDeleteWorker(worker.id)} className="delete-button">
                                                    Smazat
                                                </button>
                                                <button onClick={() => handlePriorityClick(worker.id)} className="priority-button">
                                                    Přidat prioritu
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={4}>Žádní pracovníci nenalezeni.</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    )}
                </div>

                <div className="worker-form-container">
                    <form onSubmit={handleAddWorker} className="worker-form">
                        <h3>Přidat nového pracovníka</h3>
                        <div>
                            <label>Jméno pracovníka:</label>
                            <input
                                type="text"
                                value={newWorkerName}
                                onChange={(e) => setNewWorkerName(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label>Dostupný:</label>
                            <select
                                value={newAvailable ? "true" : "false"}
                                onChange={(e) => setNewAvailable(e.target.value === "true")}
                            >
                                <option value="true">Ano</option>
                                <option value="false">Ne</option>
                            </select>
                        </div>
                        <button type="submit">Přidat pracovníka</button>
                    </form>
                </div>
            </div>

            {showPriorityModal && selectedWorkerId !== null && (
                <div className="priority-modal">
                    <div className="modal-content">
                        <h3>Přidat prioritu</h3>
                        <div>
                            <label>Pracoviště:</label>
                            <select
                                value={selectedWorkplaceId ?? ""}
                                onChange={(e) => setSelectedWorkplaceId(Number(e.target.value))}
                            >
                                <option value="" disabled>Vyberte pracoviště</option>
                                {workplaces.map((workplace) => (
                                    <option key={workplace.id} value={workplace.id}>
                                        {workplace.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label>Priorita:</label>
                            <input
                                type="number"
                                value={priority}
                                min={1}
                                onChange={(e) => setPriority(Number(e.target.value))}
                            />
                        </div>
                        <div>
                            <button onClick={handleModalSubmit}>Uložit</button>
                            <button onClick={handleModalCancel}>Zrušit</button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
};

export default WorkerView;
