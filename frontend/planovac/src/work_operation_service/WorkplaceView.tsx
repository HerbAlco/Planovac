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

const WorkplaceView: React.FC = () => {
    const { workOperationId } = useParams<{ workOperationId: string }>();
    const [workplaces, setWorkplaces] = useState<Workplace[]>([]);
    const [editingWorkplace, setEditingWorkplace] = useState<Record<number, Partial<Workplace>>>({});
    const [newWorkplace, setNewWorkplace] = useState({ name: "", maxWorkers: 1 });

    const fetchWorkplaces = useCallback(async () => {
        if (!workOperationId) return;
        try {
            const response = await axios.get<Workplace[]>(`http://localhost:8080/api/workplaces/getall/${workOperationId}`);
            setWorkplaces(response.data.sort((a, b) => a.id - b.id));
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
                                <th>Název</th>
                                <th>Max. pracovníků</th>
                                <th>Pracovníci</th>
                                <th>Akce</th>
                            </tr>
                        </thead>
                        <tbody>
                            {workplaces.length > 0 ? (
                                workplaces.map(({ id, name, maxWorkers, assignedWorkers }) => (
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
                                        <td>{(assignedWorkers || []).join(", ")}</td>
                                        <td>
                                            <button onClick={() => handleEditWorkplace(id)} className="edit-button">Upravit</button>
                                            <button onClick={() => handleDeleteWorkplace(id)} className="delete-button">Smazat</button>
                                        </td>
                                    </tr>
                                ))
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
        </div>
    );
};

export default WorkplaceView;
