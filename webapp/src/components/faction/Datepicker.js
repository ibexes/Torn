import React from "react";
import {createMuiTheme} from "@material-ui/core";
import DateFnsUtils from "@date-io/date-fns";
import {DateTimePicker, MuiPickersUtilsProvider} from "@material-ui/pickers";
import {ThemeProvider} from "@material-ui/styles";
import grey from "@material-ui/core/colors/grey";

const materialTheme = createMuiTheme({
    palette: {
        primary: grey
    },
    overrides: {
        MuiPickersDay: {
            day: {
                color: grey["900"],
            },
            current: {
                color: grey["900"],
            },
            dayDisabled: {
                color: grey["400"],
            }
        }
    }
});

function CustomDatePicker(props) {
    const {label, onChange, value, minDate, maxDate, placeholder, initialFocusedDate} = props;
    return (
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
            <ThemeProvider theme={materialTheme}>
                <DateTimePicker
                    label={label}
                    inputVariant="outlined"
                    ampm={false}
                    minutesStep={60}
                    variant="inline"
                    onChange={onChange}
                    value={value}
                    emptyLabel={placeholder}
                    minDate={minDate}
                    maxDate={maxDate}
                    views={["month", "date", "hours"]}
                    initialFocusedDate={initialFocusedDate ? initialFocusedDate : new Date()}
                    hideTabs
                    autoOk
                    disableFuture
                />
            </ThemeProvider>
        </MuiPickersUtilsProvider>
    );
}

export default CustomDatePicker;