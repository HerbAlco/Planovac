import React from "react";

interface AbsenceLegendItem {
    type: string;
    description: string;
}

const absenceLegends: AbsenceLegendItem[] = [
    { type: "Z", description: "Z volno" },
    { type: "D", description: "Dovolená" },
    { type: "NV", description: "Náhradní volno" },
    { type: "PG", description: "Paragraf" },
    { type: "PLP", description: "Preventivní prohlídka" },
    { type: "N", description: "Nemoc" },
    { type: "ST", description: "Studijní volno" },
    { type: "OCR", description: "Ošetřovné" },
    { type: "SK", description: "Školení" },
    { type: "SV", description: "Sváteční volno" },
    { type: "SH", description: "Stará hala" },
];

const AbsenceLegend: React.FC = () => {
    return (
        <div>
            <h2 className="text-lg font-semibold mb-3">Legenda absencí</h2>
            <table className="w-full border-collapse border border-gray-300">
                <thead>
                    <tr className="bg-gray-100">
                        <th className="border p-2">Kód</th>
                        <th className="border p-2">Popis</th>
                    </tr>
                </thead>
                <tbody>
                    {absenceLegends.map((item) => (
                        <tr key={item.type} className="hover:bg-gray-50">
                            <td className="border p-2 text-center font-bold">{item.type}</td>
                            <td className="border p-2">{item.description}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AbsenceLegend;