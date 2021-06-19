import React, {useEffect, useState} from "react";
import './DayActivityTable.css';
import CachedIcon from '@material-ui/icons/Cached';
import DeleteForeverOutlinedIcon from '@material-ui/icons/DeleteForeverOutlined';

function Row(props) {
    const {entryDate, activities} = props;
    return (
        <>
            <td className="date lbl"> {new Date(Date.parse(entryDate)).toLocaleDateString()} </td>
            {[ ...Array(24).keys() ].map(i => i++).map(i =>
            <td key={`td-`+entryDate+i}>
                <Hour key={`hour-`+entryDate+i} hour={i} activities={activities} entryDate={entryDate}/>
            </td>
            )}
        </>
    )
}

function Hour(props) {
    const {entryDate, hour, activities} = props;

    function isActive(increment) {
        const minute = increment * 5;

        const time = new Date();
        time.setTime(Date.parse(entryDate));
        time.setUTCHours(hour, minute);

        return activities.includes(time.toISOString());
    }

    function anyActive() {
        return [0,1,2,3,4,5,6,7,8,9,10,11].some(i => isActive(i));
    }

    return (
        <>
            {anyActive() ? (
                <>
                    <div className={isActive(0) ? 'hour active' : 'hour'}/>
                    <div className={isActive(1) ? 'hour active' : 'hour'}/>
                    <div className={isActive(2) ? 'hour active' : 'hour'}/>
                    <div className={isActive(3) ? 'hour active' : 'hour'}/>
                    <div className={isActive(4) ? 'hour active' : 'hour'}/>
                    <div className={isActive(5) ? 'hour active' : 'hour'}/>
                    <div className={isActive(6) ? 'hour active' : 'hour'}/>
                    <div className={isActive(7) ? 'hour active' : 'hour'}/>
                    <div className={isActive(8) ? 'hour active' : 'hour'}/>
                    <div className={isActive(9) ? 'hour active' : 'hour'}/>
                    <div className={isActive(10) ? 'hour active' : 'hour'}/>
                    <div className={isActive(11) ? 'hour active' : 'hour'}/>
                </>
            ) : (<> </>)
            }
        </>
    )
}

function DayActivityTable(props) {
    const {user, deleteUser} = props;
    const [data, setData] = useState([]);
    const [select, setSelect] = React.useState('');


    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        fetch(`/api/activity/user/`+user)
            .then(res => res.json())
            .then(
                (result) => {
                    //console.log(result);
                    if(result.status && result.status !== 200) {

                    } else {
                        setData(result);
                    }
                },
                (error) => {
                }
            )
    };

    useEffect(() => {
        loadData();
    }, [select]);

    return (
        <div className="activity-container">
            <div className="title-wrapper">
                <span className="title">{data.length && data[0]? data[0].name + '[' +user +']' : user}</span>
                <div className="activity-button-group">
                    <DeleteForeverOutlinedIcon className="button" onClick={() => deleteUser(user)}/>
                    <CachedIcon onClick={loadData} className="button"/>
                </div>
            </div>

            <table className='activity-table' cellSpacing="0">
                <thead>
                    <tr>
                        <th className="date lbl" >Date</th>
                        {[ ...Array(24).keys() ].map(i => i++).map(i =>
                            <th className="cell lbl" key={`header`+i}> {i < 10 ? '0' + i : i} </th>
                        )}
                    </tr>
                </thead>

                <tbody>
                    {
                        data.map(d => (
                            <tr key={`tr-`+d.date}>
                                <Row entryDate={d.date} activities={d.active} key={`row-`+d.date}/>
                            </tr>
                        ))
                    }
                </tbody>

            </table>
        </div>
    );
}

export default DayActivityTable;