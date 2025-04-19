import './MainPage.css';
import { Link } from 'react-router-dom';
import { useState, useEffect, FormEvent } from 'react';

interface WorkOperation {
    id: number;
    name: string;
    workplaces: any[];
    workers: any[];
}

const MainPage = () => {
    const [name, setName] = useState<string>('');
    const [responseMessage, setResponseMessage] = useState<string>('');
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [workOperations, setWorkOperations] = useState<WorkOperation[]>([]);

    useEffect(() => {
        const fetchWorkOperations = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/workOperation/getall');
                if (response.ok) {
                    const data: WorkOperation[] = await response.json();
                    setWorkOperations(data);
                } else {
                    throw new Error('Failed to fetch WorkOperations');
                }
            } catch (error) {
                console.error(error);
                setErrorMessage('Failed to load WorkOperations.');
            }
        };
        fetchWorkOperations();
    }, []);

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        const workOperation: WorkOperation = {
            id: 0,
            name: name,
            workplaces: [],
            workers: [],
        };

        try {
            const response = await fetch('http://localhost:8080/api/workOperation/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(workOperation),
            });

            if (response.ok) {
                const newWorkOperation: WorkOperation = await response.json();
                setResponseMessage('WorkOperation created successfully!');
                setErrorMessage('');
                setName('');
                setWorkOperations([...workOperations, newWorkOperation]);
            } else {
                throw new Error('Failed to create WorkOperation');
            }
        } catch (error) {
            console.error(error);
            setErrorMessage('Failed to create WorkOperation.');
            setResponseMessage('');
        }
    };

    return (
        <div className='mainpage-container'>
            <h1>Seznam provozů</h1>
            <div className='list-container'>
                <div>
                    <h2>Vytvořené provozy</h2>
                    {workOperations && workOperations.length > 0 ? (
                        <ul>
                            {workOperations.map((workOperation) => (
                                <li key={workOperation.id}>
                                    <Link to={`/schedule/${workOperation.id}`}>
                                        {workOperation.name}
                                    </Link>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>No WorkOperations available.</p>
                    )}
                </div>
                <div className='create-container'>
                    <form onSubmit={handleSubmit}>
                        <label htmlFor="name">Název pro nové pracoviště:</label>
                        <input
                            type="text"
                            id="name"
                            name="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                        <button type="submit">Create</button>
                    </form>
                </div>
            </div>
            {responseMessage && <p style={{ color: 'green' }}>{responseMessage}</p>}
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
        </div>
    );
};

export default MainPage;
