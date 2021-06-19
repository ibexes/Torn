import React, {useEffect, useState} from 'react';
import CustomDatePicker from "../Datepicker";
import {Paper} from "@material-ui/core";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableBody from "@material-ui/core/TableBody";
import IconButton from "@material-ui/core/IconButton";
import KeyboardArrowUpIcon from "@material-ui/icons/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@material-ui/icons/KeyboardArrowDown";
import Collapse from "@material-ui/core/Collapse";
import Box from "@material-ui/core/Box";
import './OrganisedCrimesSummary.css';
import MembersSuggest from "./MembersSuggest";
import OrganisedCrimeDrillDown from "./OrganisedCrimeDrillDown";


const header = [
    {id: 'crime', numeric: false, label: 'Crime'},
    {id: 'success', numeric: true, label: 'Success'},
    {id: 'attempts', numeric: true, label: 'Attempts'},
    {id: 'successrate', numeric: true, label: 'Success Rate'},
    {id: 'profit', numeric: true, label: 'Total Profit'},
    {id: 'profitAvg', numeric: true, label: 'Average Profit'},
    {id: 'respect', numeric: true, label: 'Total Respect'},
    {id: 'respectAvg', numeric: true, label: 'Average Respect'},
];

function Row(props) {
    const {row} = props;
    const [open, setOpen] = React.useState(false);

    return (
        <>
            <TableRow key={row.name + "-row"}>
                <TableCell scope="row" key={row.userId + "-showmore"}>
                    <IconButton aria-label="expand row" size="small"
                                onClick={() => setOpen(!open)}>
                        {open ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell scope="row" key={row.name}>
                    {row.name}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-success"}>
                    {row.successes}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-attempts"}>
                    {row.attempts}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-success-rate"}>
                    {row.attempts > 0 ? Math.round(row.successes / row.attempts * 100) + '%' : 'N/A'}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-profit"}>
                    {row.attempts > 0 ? new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'})
                        .format(row.profit)
                        .replace(/\D00(?=\D*$)/, '') : 'N/A'}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-profit-avg"}>
                    {row.successes > 0 ? new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'})
                        .format(Math.round(row.profit / row.successes))
                        .replace(/\D00(?=\D*$)/, '') : 'N/A'}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-respect"}>
                    {row.attempts > 0 ? row.respect : 'N/A'}
                </TableCell>
                <TableCell align="right" scope="row" key={row.name + "-respect-avg"}>
                    {row.successes > 0 ? (row.respect / row.successes).toFixed(1) : 'N/A'}
                </TableCell>
            </TableRow>
            {!open ? <></> : (
                <TableRow key={row.name + "-breakdown"}>
                    <TableCell colSpan="9">
                        <Collapse in={open} timeout='auto' unmountOnExit>
                            <Box margin={1}>
                                <OrganisedCrimeDrillDown history={row.history}/>
                            </Box>
                        </Collapse>
                    </TableCell>
                </TableRow>
            )}
        </>
    );
}

function OrganisedCrimesSummary() {
    const [selectedStartDate, setSelectedStartDate] = useState(null);
    const [minStartDate, setMinStartDate] = useState(new Date());
    const [selectedEndDate, setSelectedEndDate] = useState(new Date());
    const [summaryData, setSummaryData] = useState([]);
    const [search, setSearch] = useState();

    useEffect(() => {
        loadTime();
    }, []);

    useEffect(() => {
        loadSummaryData();
    }, [selectedStartDate]);

    useEffect(() => {
        loadSummaryData();
    }, [selectedEndDate]);

    useEffect(() => {
        loadSummaryData();
    }, [search]);

    const loadTime = () => {
        fetch("api/faction/ocs/startDate")
            .then(res => res.json())
            .then(
                (result) => {
                    setSelectedStartDate(new Date(result));
                    setMinStartDate(new Date(result));
                },
                (error) => {
                    setSelectedStartDate(new Date());
                    setMinStartDate(new Date());
                }
            )
    };

    const loadSummaryData = () => {
        if (selectedEndDate == null || selectedStartDate == null) {
            return;
        }

        if (search == null || search.replace( /^\D+/g, '') === "") {
            fetch(`api/faction/ocs/summary/${selectedStartDate.toISOString().slice(0, 16)}/${selectedEndDate.toISOString().slice(0, 16)}`)
                .then(res => res.json())
                .then(
                    (result) => {
                        if (result !== null && result.stats !== null) {
                            setSummaryData(Object.values(result.stats));
                        }
                    },
                    (error) => {
                        setSummaryData([]);
                    }
                )
        } else {
            fetch(`api/faction/ocs/summary/${search.replace( /^\D+/g, '')}/${selectedStartDate.toISOString().slice(0, 16)}/${selectedEndDate.toISOString().slice(0, 16)}`)
                .then(res => res.json())
                .then(
                    (result) => {
                        if (result !== null && result.stats !== null) {
                            setSummaryData(Object.values(result.stats));
                        }
                    },
                    (error) => {
                        setSummaryData([]);
                    }
                )
        }


    };

    return (
        <>
            <div>
                <div className="table-control">
                    <div className="date-picker-container">
                        <CustomDatePicker
                            label="Start"
                            value={selectedStartDate}
                            onChange={setSelectedStartDate}
                            minDate={minStartDate}
                            placeholder="Select start of range"
                        />
                        <CustomDatePicker
                            label="End"
                            value={selectedEndDate}
                            onChange={setSelectedEndDate}
                            minDate={selectedStartDate ? selectedStartDate : minStartDate}
                            placeholder="Select end of range"
                        />
                    </div>

                    <MembersSuggest onChange={setSearch}/>
                </div>
            </div>

            <TableContainer component={Paper}>
                <Table aria-label="contributions" size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>

                            </TableCell>
                            {header.map((cell) => (
                                <TableCell key={cell.id} align={cell.numeric ? 'right' : 'left'}>
                                    {cell.label}
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {summaryData
                            .map((row) => (
                                <Row row={row} />
                            ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
}

export default OrganisedCrimesSummary;