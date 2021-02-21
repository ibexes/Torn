import React, {useEffect, useState} from 'react';
import {FaSpinner} from 'react-icons/fa';
import TableContainer from "@material-ui/core/TableContainer";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import {Paper} from "@material-ui/core";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import TablePagination from "@material-ui/core/TablePagination";
import './FactionContributionsSummary.css';
import TextField from "@material-ui/core/TextField";
import IconButton from "@material-ui/core/IconButton";
import KeyboardArrowDownIcon from '@material-ui/icons/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp';
import Collapse from "@material-ui/core/Collapse";
import Box from "@material-ui/core/Box";
import FactionContributionsGraph from "./FactionContributionsGraph";
import CustomDatePicker from "../Datepicker";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";

function descendingComparator(a, b, orderBy, child) {
    let x = a[orderBy];
    let y = b[orderBy];

    if (child !== null) {
        x = a[orderBy][child];
        y = b[orderBy][child];
    }

    x = x != null ? x : -1;
    y = y != null ? y : -1;

    if (y < x) {
        return -1;
    }
    if (y > x) {
        return 1;
    }
    return 0;
}

function getComparator(order, orderBy, child) {
    return order === 'desc'
        ? (a, b) => descendingComparator(a, b, orderBy, child)
        : (a, b) => -descendingComparator(a, b, orderBy, child);
}

function stableSort(array, comparator) {
    const stabilizedThis = array.map((el, index) => [el, index]);
    stabilizedThis.sort((a, b) => {
        const order = comparator(a[0], b[0]);
        if (order !== 0) return order;
        return a[1] - b[1];
    });
    return stabilizedThis.map((el) => el[0]);
}

const header = [
    {id: 'name', numeric: false, label: 'User', stat: false},
    {id: 'gymStrength', numeric: true, label: 'Strength', stat: true},
    {id: 'gymSpeed', numeric: true, label: 'Speed', stat: true},
    {id: 'gymDefence', numeric: true, label: 'Defence', stat: true},
    {id: 'gymDexterity', numeric: true, label: 'Dexterity', stat: true},
    {id: 'gymTotal', numeric: true, label: 'Total', stat: true},
    {id: 'lastAction', numeric: false, label: 'Last Action', stat: false}
];

function Row(props) {
    const {row, selectedStartDate, selectedEndDate} = props;
    const [open, setOpen] = React.useState(false);

    return (
        <>
            <TableRow key={row.name} className={row.inFaction? "" : "not-in-faction"}>
                <TableCell scope="row" key={row.userId + "-showmore"}>
                    <IconButton aria-label="expand row" size="small"
                                onClick={() => setOpen(!open)}>
                        {open ? <KeyboardArrowUpIcon/> : <KeyboardArrowDownIcon/>}
                    </IconButton>
                </TableCell>
                <TableCell scope="row" key={row.userId}>
                    {row.name} [<a className="player-link"
                                   href={"https://www.torn.com/profiles.php?XID=" + row.userId}>{row.userId}</a>]
                </TableCell>
                <TableCell scope="row" key={row.userId + "-gymStrength"}>
                    {row['gymStrength'].difference}
                </TableCell>
                <TableCell scope="row" key={row.userId + "-gymSpeed"}>
                    {row['gymSpeed'].difference}
                </TableCell>
                <TableCell scope="row" key={row.userId + "-gymDefence"}>
                    {row['gymDefence'].difference}
                </TableCell>
                <TableCell scope="row" key={row.userId + "-gymDexterity"}>
                    {row['gymDexterity'].difference}
                </TableCell>
                <TableCell scope="row" key={row.userId + "-gymTotal"}>
                    {row['gymTotal'].difference}
                </TableCell>
                <TableCell scope="row" key={row.userId + "-lastAction"}>
                    {row.lastAction ? new Date(row.lastAction).toLocaleString() : ''}
                </TableCell>
            </TableRow>
            {!open ? <></> : (
                <TableRow className={row.inFaction? "" : "not-in-faction"}>
                    <TableCell colSpan="8">
                        <Collapse in={open} timeout='auto' unmountOnExit>
                            <Box margin={1}>
                                <FactionContributionsGraph userId={row.userId} selectedStartDate={selectedStartDate}
                                                           selectedEndDate={selectedEndDate}/>
                            </Box>
                        </Collapse>
                    </TableCell>
                </TableRow>
            )}
        </>
    );
}

function FactionContributionsSummary() {
    const [isLoading, setIsLoading] = useState(true);
    const [contributors, setContributors] = useState([]);
    const [times, setTimes] = useState([]);
    const [selectedStartDate, setSelectedStartDate] = useState(null);
    const [selectedEndDate, setSelectedEndDate] = useState(null);
    const [order, setOrder] = useState('desc');
    const [orderBy, setOrderBy] = useState('gymTotal');
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(25);
    const [search, setSearch] = React.useState('');
    const [hideNotInFaction, setHideNotInFaction] = React.useState(true);

    useEffect(() => {
        fetchTimes();
    }, []);

    const updateTable = () => {
        if (selectedEndDate == null || selectedStartDate == null) {
            return;
        }

        setIsLoading(true);
        fetch(`/api/faction/contributions/summary/${selectedStartDate.toISOString().slice(0, 16)}/${selectedEndDate.toISOString().slice(0, 16)}`)
            .then(res => res.json())
            .then(
                (result) => {
                    setIsLoading(false);
                    setContributors(result);
                },
                (error) => {
                    setIsLoading(false);
                    setContributors([]);
                }
            )
    };

    const fetchTimes = () => {
        fetch("/api/faction/contributions/intervals")
            .then(res => res.json())
            .then(
                (result) => {
                    setTimes(result);
                    setIsLoading(false);
                },
                (error) => {
                    setTimes([]);
                    setIsLoading(false);
                }
            )
    };

    const createSortHandler = (property) => (event) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSearchChange = (event) => {
        setSearch(event.target.value);
    };

    const getMinTime = (times) => {
        return times == null || times.length === 0 ? null : new Date(Math.min.apply(null, times)).setMinutes(0);
    };

    const getMaxTime = (times) => {
        let date = times == null || times.length === 0 ? null : new Date(Math.max.apply(null, times));
        if(date !== null) {
            date.setMinutes(0);
            date.setSeconds(0);
            date.setMilliseconds(0);
        }
        return date;
    };

    useEffect(() => {
        updateTable();
    }, [selectedStartDate]);

    useEffect(() => {
        updateTable();
    }, [selectedEndDate]);

    const filteredContributors = (contributors) => {
        return contributors
            .filter(row => search === "" || `${row.name} [${row.userId}]`.toLowerCase().indexOf(search.toLowerCase()) !== -1)
            .filter(row => !hideNotInFaction || row.inFaction)
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
                            minDate={getMinTime(times)}
                            maxDate={getMaxTime(times
                                .filter((time) => selectedEndDate == null ? true : selectedEndDate.getTime() > time))}
                            placeholder="Select start of range"
                            initialFocusedDate={selectedEndDate ? selectedEndDate : getMinTime(times)}
                        />
                        <CustomDatePicker
                            label="End"
                            value={selectedEndDate}
                            onChange={setSelectedEndDate}
                            minDate={getMinTime(times
                                .filter((time) => selectedStartDate == null ? true : selectedStartDate.getTime() < time))}
                            maxDate={getMaxTime(times)}
                            placeholder="Select end of range"
                            initialFocusedDate={selectedStartDate ? selectedStartDate : getMaxTime(times)}
                        />
                    </div>


                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={hideNotInFaction}
                                onChange={(event) => setHideNotInFaction(event.target.checked)}
                                name="hide-not-in-faction"
                                color="inherit"
                            />
                        }
                        label="Hide Ex-members"
                    />

                    <TextField id="search" label="Search for Member" variant="outlined" value={search}
                               onChange={handleSearchChange}/>
                </div>

                {isLoading ? <FaSpinner icon="spinner" className="spinner"/> : (
                    <>
                        <TableContainer component={Paper}>
                            <Table aria-label="contributions" size="small">
                                <TableHead>
                                    <TableRow>
                                        <TableCell>

                                        </TableCell>
                                        {header.map((cell) => (
                                            <TableCell
                                                key={cell.id}
                                                sortDirection={orderBy === cell.id ? order : false}
                                            >
                                                <TableSortLabel
                                                    active={orderBy === cell.id}
                                                    direction={orderBy === cell.id ? order : 'asc'}
                                                    onClick={createSortHandler(cell.id)}
                                                >
                                                    {cell.label}
                                                </TableSortLabel>
                                            </TableCell>
                                        ))}
                                    </TableRow>
                                </TableHead>
                                <TableBody>
                                    {filteredContributors(stableSort(contributors, getComparator(order, orderBy, header.find(head => head.id === orderBy).stat ? 'difference' : null)))
                                        .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                        .map((row) => (
                                            <Row key={row.userId + "-row"} row={row}
                                                 selectedStartDate={selectedStartDate}
                                                 selectedEndDate={selectedEndDate}/>
                                        ))}
                                </TableBody>
                            </Table>
                        </TableContainer>
                        <TablePagination
                            rowsPerPageOptions={[25, 50, 100]}
                            component="div"
                            count={filteredContributors(contributors).length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onChangePage={handleChangePage}
                            onChangeRowsPerPage={handleChangeRowsPerPage}
                        />
                    </>
                )}
            </div>
        </>
    );
}

export default FactionContributionsSummary;