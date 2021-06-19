import React, {useEffect, useState} from "react";
import TextField from "@material-ui/core/TextField";
import DayActivityTable from "./DayActivityTable";
import AddIcon from '@material-ui/icons/Add';
import ClearIcon from '@material-ui/icons/Clear';

function UserActivity() {
    const [users, setUsers] = useState([]);
    const [userSelection, setUserSelection] = useState("");
    const [factionSelection, setFactionSelection] = useState("");

    const handleUserSelectChange = (event) => {
        setUserSelection(event.target.value);
    };

    const handleFactionSelectChange = (event) => {
        setFactionSelection(event.target.value);
    };

    const handleUserKeyDown = (event) => {
        if (event.key === 'Enter') {
            addUser();
            event.target.blur();
        }
    };

    const handleFactionKeyDown = (event) => {
        if (event.key === 'Enter') {
            addFaction();
            event.target.blur();
        }
    };

    const addUser = () => {
        if (!users.includes(userSelection) && userSelection !== "") {
            setUsers(users.concat(userSelection));
        }
    };

    const removeUser = (user) => {
        const array = [...users];
        const index = array.indexOf(user);
        if (index !== -1) {
            array.splice(index, 1);
            setUsers(array);
        }
    };

    const clearAll = () => {
        setUsers([]);
    };

    const addFaction = () => {
        if (factionSelection === "") return;

        fetch(`/api/activity/faction/`+factionSelection)
            .then(res => res.json())
            .then(
                (result) => {
                    if(result.status && result.status !== 200) {

                    } else {
                        setUsers(users.concat(result));
                    }
                },
                (error) => {
                }
            );
        setFactionSelection("");
    };

    useEffect(() => {
        setUserSelection("");
    }, [users]);

    return (
        <>
            <div className="main-content">
                <TextField id="search" label="Select User ID" variant="outlined" value={userSelection}
                           onChange={handleUserSelectChange} onKeyDown={handleUserKeyDown}/>
                <AddIcon onClick={addUser} className="button" />

                <TextField id="faction" label="Select Faction ID" variant="outlined" value={factionSelection}
                           onChange={handleFactionSelectChange} onKeyDown={handleFactionKeyDown}/>
                <AddIcon onClick={addFaction} className="button" />

                <ClearIcon onClick={clearAll} className="button" />

                {
                    users.map(user => (
                        <DayActivityTable user={user} key={'user-'+user} deleteUser={removeUser}/>
                    ))
                }
            </div>
        </>
    )
}

export default UserActivity;