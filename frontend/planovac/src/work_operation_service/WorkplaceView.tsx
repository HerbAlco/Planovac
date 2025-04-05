import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import { Link, useParams } from "react-router-dom";
import "./WorkplaceView.css";
import { toast, ToastContainer } from "react-toastify";

interface Workplace {
    id: number;
    name: string;
    maxWorkers: number;
    assignedWorkers: string[];
}

interface Worker {
    id: number;
    name: string;
    available: boolean;
    priorities: {
        workplaceId: number;
        priority: number;
    }[];
}

const WorkplaceView: React.FC = () => {
    const { workOperationId } = useParams<{ workOperationId: string }>();
    const [workplaces, setWorkplaces] = useState<Workplace[]>([]);
    const [workers, setWorkers] = useState<Worker[]>([]);
    const [editingWorkplace, setEditingWorkplace] = useState<Record<number, Partial<Workplace>>>({});
    const [newWorkplace, setNewWorkplace] = useState({ name: "", maxWorkers: 1 });
    const [showPriorityModal, setShowPriorityModal] = useState(false);
    const [selectedWorkerId, setSelectedWorkerId] = useState<number | null>(null);
    const [selectedWorkplaceId, setSelectedWorkplaceId] = useState<number | null>(null);
    const [priority, setPriority] = useState(1);

    const fetchWorkplaces = useCallback(async () => {
        if (!workOperationId) return;
        try {
            const responseForWorkplaces = await axios.get<Workplace[]>(`http://localhost:8080/api/workplaces/getall/${workOperationId}`);
            setWorkplaces(responseForWorkplaces.data.sort((a, b) => a.id - b.id));

            const responseForWorkers = await axios.get<Worker[]>(
                `http://localhost:8080/api/workOperation/workersfromworkoperation/${workOperationId}`
            );
            setWorkers(responseForWorkers.data.sort((a, b) => a.name.localeCompare(b.name)));
            
        } catch {
            console.error("Error fetching workplaces.");
        }
    }, [workOperationId]);

    useEffect(() => {
        fetchWorkplaces();
    }, [fetchWorkplaces]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>, workplaceId?: number) => {
        const { name, value } = e.target;
        if (workplaceId) {
            setEditingWorkplace(prev => ({
                ...prev,
                [workplaceId]: {
                    ...workplaces.find(wp => wp.id === workplaceId),
                    ...prev[workplaceId],
                    [name]: value
                }
            }));
        } else {
            setNewWorkplace(prev => ({ ...prev, [name]: value }));
        }
    };

    const showMessage = (message: string, isError: boolean) => {
        if (isError) {
            toast.error(message);  // Chybová notifikace
        } else {
            toast.success(message);  // Úspěšná notifikace
        }
    };

    const handleAddWorkplace = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!workOperationId) return;

        try {
            const response = await axios.post(`http://localhost:8080/api/workplaces/create/${workOperationId}`, {
                ...newWorkplace,
                workOperationId: parseInt(workOperationId),
            });
            setWorkplaces([...workplaces, response.data]);
            setNewWorkplace({ name: "", maxWorkers: 1 });
            showMessage("✅ Pracoviště úspěšně přidáno.", false);
        } catch {
            showMessage("❌ Chyba při přidávání pracoviště.", true);
        }
    };

    const handleEditWorkplace = async (workplaceId: number) => {
        const updatedData = editingWorkplace[workplaceId];
        if (!updatedData) return;

        const workplaceUpdate = { ...updatedData, id: workplaceId };

        try {
            await axios.put(`http://localhost:8080/api/workplaces/update/${workOperationId}`, workplaceUpdate);
            setWorkplaces(prev =>
                prev.map(wp => (wp.id === workplaceId ? { ...wp, ...workplaceUpdate } : wp))
            );
            setEditingWorkplace(prev => {
                const newState = { ...prev };
                delete newState[workplaceId];
                return newState;
            });
            showMessage("✅ Pracoviště úspěšně upraveno.", false);
        } catch {
            showMessage("❌ Chyba při úpravě pracoviště.", true);
        }
    };

    const handleDeleteWorkplace = async (workplaceId: number) => {

        try {
            await axios.delete(`http://localhost:8080/api/workplaces/delete/${workplaceId}`);
            setWorkplaces(prev => prev.filter(wp => wp.id !== workplaceId));
            showMessage("✅ Pracoviště úspěšně smazáno.", false);
        } catch {
            showMessage("❌ Chyba při mazání pracoviště.", true);
        }
    };

    const handlePriorityClick = (workplaceId: number) => {
        setSelectedWorkplaceId(workplaceId);
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
        <div className="workplace-container">
            <nav className="workplace-nav">
                <Link to={`/workerview/${workOperationId}`}>Správa pracovníků</Link>
            </nav>
            <h2>Správa pracovišť</h2>
            <ToastContainer />
            <div className="workplace-layout">
                <div className="workplace-table-container">
                    <table className="workplace-table">
                        <thead>
                            <tr>
                                <th className="name-column">Název</th>
                                <th className="max-workers-column">Počet</th>
                                <th className="assigned-workers-column">Pracovníci</th>
                                <th className="actions-column">Akce</th>
                            </tr>
                        </thead>
                        <tbody>
                            {workplaces.length > 0 ? (
                                workplaces.map(({ id, name, maxWorkers }) => {
                                    const assignedWorkers = workers
                                        .map(worker => {
                                            const priority = worker.priorities.find(p => p.workplaceId === id);
                                            return priority ? { name: worker.name, priority: priority.priority } : null;
                                        })
                                        .filter((item): item is { name: string; priority: number } => item !== null)
                                        .sort((a, b) => a.priority - b.priority)
                                        .map(({ name, priority }) => `${name} (${priority})`)
                                        .join(", ");
                                    
                                    return (
                                        <tr key={id}>
                                            <td>
                                                <input
                                                    type="text"
                                                    name="name"
                                                    value={editingWorkplace[id]?.name ?? name}
                                                    onChange={(e) => handleInputChange(e, id)}
                                                />
                                            </td>
                                            <td>
                                                <input
                                                    type="number"
                                                    name="maxWorkers"
                                                    value={editingWorkplace[id]?.maxWorkers ?? maxWorkers}
                                                    onChange={(e) => handleInputChange(e, id)}
                                                />
                                            </td>
                                            <td>{assignedWorkers}</td>
                                            <td className="actions-button">
                                                <button onClick={() => handleEditWorkplace(id)} className="edit-button">Upravit</button>
                                                <button onClick={() => handleDeleteWorkplace(id)} className="delete-button">Smazat</button>
                                                <button onClick={() => handlePriorityClick(id)} className="priority-button">
                                                    Přidat pracovníka
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })
                            ) : (
                                <tr>
                                    <td colSpan={4}>Žádná pracoviště nenalezena.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
                <div className="workplace-form-container">
                    <form onSubmit={handleAddWorkplace} className="workplace-form">
                        <h3>Přidat nové pracoviště</h3>
                        <input
                            type="text"
                            name="name"
                            value={newWorkplace.name}
                            onChange={handleInputChange}
                            placeholder="Název pracoviště"
                            required
                        />
                        <input
                            type="number"
                            name="maxWorkers"
                            value={newWorkplace.maxWorkers}
                            onChange={handleInputChange}
                            placeholder="Maximální počet pracovníků"
                            required
                        />
                        <button type="submit">Přidat</button>
                    </form>
                </div>
            </div>

            {showPriorityModal && selectedWorkplaceId !== null && (
                <div className="priority-modal">
                    <div className="modal-content">
                        <h3>Přidat pracovníka k {workplaces.find((workplace) => workplace.id === selectedWorkplaceId)?.name }</h3>
                        <div>
                            <label>Pracovník:</label>
                            <select
                                value={selectedWorkerId ?? ""}
                                onChange={(e) => setSelectedWorkerId(Number(e.target.value))}
                            >
                                <option value="" disabled>Vyberte pracovníka</option>
                                {workers.map((worker) => (
                                    <option key={worker.id} value={worker.id}>
                                        {worker.name}
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

export default WorkplaceView;
