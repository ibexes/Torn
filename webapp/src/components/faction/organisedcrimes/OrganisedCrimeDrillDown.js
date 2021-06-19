import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import Table from "@material-ui/core/Table";
import React from "react";
import TablePagination from "@material-ui/core/TablePagination";
import TableContainer from "@material-ui/core/TableContainer";

function OrganisedCrimeDrillDown(props) {
    const {history} = props;
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    return (
        <TableContainer>
            <Table aria-label="contributions" size="small">
                <TableHead>
                    <TableRow>
                        <TableCell>
                            Planned At
                        </TableCell>
                        <TableCell>
                            Planned By
                        </TableCell>
                        <TableCell>
                            Initiated At
                        </TableCell>
                        <TableCell>
                            Initiated By
                        </TableCell>
                        <TableCell>
                            Success
                        </TableCell>
                        <TableCell align="right">
                            Money Gained
                        </TableCell>
                        <TableCell align="right">
                            Respect Gained
                        </TableCell>
                        <TableCell>
                            Participants
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {history
                        .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                        .map((cell) => (
                            <TableRow className={cell.success ? 'success' : 'fail'}>
                                <TableCell>
                                    {new Date(cell.plannedAt).toLocaleString()}
                                </TableCell>
                                <TableCell>
                                    {cell.plannedBy.name} [{cell.plannedBy.userId}]
                                </TableCell>
                                <TableCell>
                                    {new Date(cell.initiatedAt).toLocaleString()}
                                </TableCell>
                                <TableCell>
                                    {cell.initiatedBy.name} [{cell.initiatedBy.userId}]
                                </TableCell>
                                <TableCell>
                                    {cell.success ? 'YES' : 'NO'}
                                </TableCell>
                                <TableCell align="right">
                                    {cell.moneyGained ? new Intl.NumberFormat('en-US', {
                                        style: 'currency',
                                        currency: 'USD'
                                    })
                                        .format(cell.moneyGained)
                                        .replace(/\D00(?=\D*$)/, '') : 'N/A'}
                                </TableCell>
                                <TableCell align="right">
                                    {cell.respectGained ? cell.respectGained : 'N/A'}
                                </TableCell>
                                <TableCell>
                                    {cell.participants
                                        .map(participant => (
                                            <>{participant.name} [{participant.userId}] <br/></>
                                        ))}
                                </TableCell>
                            </TableRow>
                        ))}
                </TableBody>
            </Table>
            <TablePagination
                rowsPerPageOptions={[10, 25, 50, history.length]}
                component="div"
                count={history.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onChangePage={handleChangePage}
                onChangeRowsPerPage={handleChangeRowsPerPage}
            />
        </TableContainer>)
}

export default OrganisedCrimeDrillDown;