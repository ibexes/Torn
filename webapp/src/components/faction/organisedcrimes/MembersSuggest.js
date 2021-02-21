import React, {useEffect, useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import CircularProgress from '@material-ui/core/CircularProgress';

function MembersSuggest(props) {
    const {onChange} = props;
    const [open, setOpen] = useState(false);
    const [options, setOptions] = useState([]);
    const loading = open && options.length === 0;

    useEffect(() => {
        if (!loading) {
            return;
        }

        fetch('/api/faction/ocs/members')
            .then(response => response.json())
            .then(members => {
                setOptions(members);
            });
    }, [loading]);

    useEffect(() => {
        if (!open) {
            setOptions([]);
        }
    }, [open]);

    return (
        <Autocomplete
            id="member-autosuggest"
            freeSolo
            open={open}
            style={{ width: 250 }}
            onOpen={() => {
                setOpen(true);
            }}
            onClose={() => {
                setOpen(false);
            }}
            getOptionSelected={(option, value) => option.name === value.name}
            getOptionLabel={(option) => option.name + " [" + option.userId + "]"}
            options={options}
            loading={loading}
            onChange={(event, value) =>
                value == null? onChange("") : value.userId == null? onChange(value) : onChange((value.userId).toString())
            }
            renderInput={(params) => (
                <TextField
                    {...params}
                    label="Filter by User"
                    variant="outlined"
                    InputProps={{
                        ...params.InputProps,
                        endAdornment: (
                            <React.Fragment>
                                {loading ? <CircularProgress color="inherit" size={20} /> : null}
                                {params.InputProps.endAdornment}
                            </React.Fragment>
                        ),
                    }}
                />
            )}
        />
    );
}

export default MembersSuggest;