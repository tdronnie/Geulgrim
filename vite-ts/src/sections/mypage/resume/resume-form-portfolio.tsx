import { useState, useEffect, Fragment } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

import Table from '@mui/material/Table';
import Paper from '@mui/material/Paper';
import TableRow from '@mui/material/TableRow';
import Checkbox from '@mui/material/Checkbox';
import Collapse from '@mui/material/Collapse';
import TableCell from '@mui/material/TableCell';
import TableBody from '@mui/material/TableBody';
import IconButton from '@mui/material/IconButton';
import TableContainer from '@mui/material/TableContainer';
import Pagination, { paginationClasses } from '@mui/material/Pagination';

import { useRowState } from 'src/hooks/useRowState';
import { usePreviewState } from 'src/hooks/usePreviewState';

import Iconify from 'src/components/iconify';
import Scrollbar from 'src/components/scrollbar';
import {
  emptyRows,
  getComparator,
  TableEmptyRows,
  TableHeadCustom
} from 'src/components/table';

import ResumeFormPortfolioTable from './resume-form-portfolio-table';
import ResumeFormPortfolioUserPreview from './resume-form-portfolio-user-preview';
import ResumeFormPortfolioServicePreview from './resume-form-portfolio-service-preview';

// ----------------------------------------------------------------------


type CustomRowDataType = {
  pofolName : string,
  pofolId : number,
  createAt : Date,
  updateAt : Date,
  format : string
}

type CustomInputType = {
  pofolName : string,
  pofolId : number,
  createAt : string,
  updateAt : string,
  format : string
}

type Props = {
  portfolDatas :CustomInputType[]
}

const custom_TABLE_HEAD = [
  { id: 'pofolName', label: '포트폴리오 제목', align: 'left', minWidth:'150px' },
  { id: 'createAt', label: '생성일', align: 'center',minWidth:'150px' },
  { id: 'updateAt', label: '최근 수정일', align: 'center', minWidth:'120px' },
  { id: 'detail', label: '', align: 'center', width:'8px'}
]
// ----------------------------------------------------------------------

export default function RHFSelectPortfolio({portfolDatas}:Props) {
  const { control, setValue, getValues } = useFormContext()

  const { openRows, toggleRow } = useRowState()
  const { openPreview, togglePreview } = usePreviewState()

  const [page, setPage] = useState<number>(1);
  const [tableData, setTableData] = useState<CustomRowDataType[]>([]);

  const denseHeight = 34;

  useEffect(() => {
    const dummyDataChangeType = portfolDatas.map(item => ({
      pofolName: item.pofolName,
      pofolId: item.pofolId,
      createAt: new Date(item.createAt),
      updateAt: new Date(item.updateAt),
      format: item.format
    }));
    setTableData(dummyDataChangeType);
  }, [portfolDatas]);

  const table = ResumeFormPortfolioTable({
    defaultOrderBy: 'createAt',
  });

  const dataFiltered = applyFilter({
    inputData: tableData,
    comparator: getComparator(table.order, table.orderBy),
  });

  const handleChangePage = (event: React.ChangeEvent<unknown>, newPage: number) => {
    setPage(newPage);
  };


  return (
    <div>
      <TableContainer sx={{ position: 'relative', overflow: 'unset' }}>
        <Scrollbar>
          <Table size={table.dense ? 'small' : 'medium'} sx={{ minWidth: 420 }}>

            {/* 테이블 헤더 */}
            <TableHeadCustom
              order={table.order}
              orderBy={table.orderBy}
              headLabel={custom_TABLE_HEAD}
              rowCount={dataFiltered.length}
              numSelected={table.selected.length}
              onSort={table.onSort}
              onSelectAllRows={(checked) => {
                const newValues = checked ? dataFiltered.map((row) => row.pofolId) : [];
                setValue("portfolioIds", newValues);
                table.onSelectAllRows(
                  checked,
                  dataFiltered.map((row) => row.pofolId)
                )
              }}
            />

            {/* 테이블 */}
            <TableBody>
              {dataFiltered
                .slice(
                  (page - 1) * table.rowsPerPage,
                  (page - 1) * table.rowsPerPage + table.rowsPerPage
                )
                .map((row) => {
                  console.log(row)
                  return (
                  <Fragment key={row.pofolId}>
                    <TableRow
                      hover
                      key={row.pofolId }
                      onClick={() => {
                        // 클릭시 form의 value에 접속해 변경
                        const newValues = getValues("portfolioIds").includes(row.pofolId) ?
                        getValues("portfolioIds").filter((id:number) => id !== row.pofolId)
                        : [...getValues("portfolioIds"), row.pofolId];
                        setValue("portfolioIds", newValues);
                        table.onSelectRow(row.pofolId)
                      }}
                      selected={table.selected.includes(row.pofolId)}
                    >
                      <TableCell padding="checkbox">

                        <Controller
                          name="portfolioIds"
                          control={control}
                          render={({ field: { onChange, onBlur, value } }) => (
                            <Checkbox
                              onBlur={onBlur}
                              onChange={(event) => {
                                let updatedValue;
                                if (event.target.checked) {
                                  updatedValue = [...value, row.pofolId].sort((a, b) => a - b);
                                } else {
                                  updatedValue = value.filter((id: number) => id !== row.pofolId);
                                }
                                onChange(updatedValue);
                                setValue("portfolioIds", updatedValue);
                              }}
                              checked={value.includes(row.pofolId)}
                      />
                          )}
                        />
                      </TableCell>
                      <TableCell> {row.pofolName } </TableCell>
                      <TableCell align="center">{row.createAt.toLocaleDateString() }</TableCell>
                      <TableCell align="center">{row.updateAt.toLocaleDateString()}</TableCell>
                      <TableCell>
                        <IconButton
                          size="small"
                          color={openRows[row.pofolId] ? 'inherit' : 'default'}
                          onClick={(event) => {
                            event.stopPropagation()
                            toggleRow(row.pofolId)}}
                        >
                          <Iconify
                            icon={openRows[row.pofolId] ? 'eva:arrow-ios-upward-fill' : 'eva:arrow-ios-downward-fill'}
                          />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell sx={{ py: 0 }} colSpan={6}>
                        <Collapse in={openRows[row.pofolId]} unmountOnExit>
                          <Paper
                            variant="outlined"
                            sx={{
                              py: 2,
                              my: 2,
                              borderRadius: 1.5,
                              ...(openRows[row.pofolId] && {
                                boxShadow: (theme) => theme.customShadows.z20,
                              }),
                            }}
                          >
                              {row.format === 'USER' ?
                              <ResumeFormPortfolioUserPreview portfolId={row.pofolId}/>
                              :
                              <ResumeFormPortfolioServicePreview portfolId={row.pofolId}/>
                              }

                          </Paper>
                        </Collapse>
                      </TableCell>
                    </TableRow>
                    </Fragment>
                )})}

              <TableEmptyRows
                height={denseHeight}
                emptyRows={emptyRows(table.page, table.rowsPerPage, dataFiltered.length)}
              />
            </TableBody>
          </Table>
        </Scrollbar>
      </TableContainer>
      {/* 페이지네이션 */}
      <Pagination
        count={Math.ceil(dataFiltered.length / 5)}
        defaultPage={1}
        page={page}
        onChange={handleChangePage}
        siblingCount={1}
        sx={{
          mt: 3,
          mb: 3,
          [`& .${paginationClasses.ul}`]: {
            justifyContent: 'center',
          },
        }}
      />

      {/* 선택 포폴 */}
      {table.selected}
      <TableContainer sx={{ position: 'relative', overflow: 'unset' }}>
        <Scrollbar>
          <Table size={table.dense ? 'small' : 'medium'} sx={{ minWidth: 420 }}>
            {/* 테이블 */}
            <TableBody>
              {table.selected
                .slice(
                  (page - 1) * table.rowsPerPage,
                  (page - 1) * table.rowsPerPage + table.rowsPerPage
                )
                .map((rowNum) => {
                  const data = tableData.find(portfol => portfol.pofolId === rowNum);
                  return (
                  <Fragment key={rowNum}>
                    <TableRow
                      key={rowNum}
                      onClick={() => {
                        // 클릭시 form의 value에 접속해 변경
                        const newValues = getValues("portfolioIds").includes(rowNum) ?
                        getValues("portfolioIds").filter((id:number) => id !== rowNum)
                        : [...getValues("portfolioIds"), rowNum];
                        setValue("portfolioIds", newValues);
                        table.onSelectRow(rowNum)
                      }}
                    >
                      <TableCell padding="checkbox">

                        <Controller
                          name="portfolioIds"
                          control={control}
                          render={({ field: { onChange, onBlur, value } }) => (
                            <Checkbox
                              onBlur={onBlur}
                              onChange={(event) => {
                                let updatedValue;
                                if (event.target.checked) {
                                  updatedValue = [...value, rowNum].sort((a, b) => a - b);
                                } else {
                                  updatedValue = value.filter((id: number) => id !== rowNum);
                                }
                                onChange(updatedValue);
                                setValue("portfolioIds", updatedValue);
                              }}
                              checked={value.includes(rowNum)}
                      />
                          )}
                        />
                      </TableCell>
                      <TableCell> {data?.pofolName } </TableCell>
                      <TableCell>
                        <IconButton
                          size="small"
                          color={openPreview[rowNum] ? 'inherit' : 'default'}
                          onClick={(event) => {
                            event.stopPropagation()
                            togglePreview(rowNum)}}
                        >
                          <Iconify
                            icon={openPreview[rowNum] ? 'eva:arrow-ios-upward-fill' : 'eva:arrow-ios-downward-fill'}
                          />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                    <TableRow>
                      <TableCell sx={{ py: 0 }} colSpan={6}>
                        <Collapse in={openPreview[rowNum]} unmountOnExit>
                          <Paper
                            variant="outlined"
                            sx={{
                              py: 2,
                              my: 2,
                              borderRadius: 1.5,
                              ...(openPreview[rowNum] && {
                                boxShadow: (theme) => theme.customShadows.z20,
                              }),
                            }}
                          >
                              {data?.format === 'USER' ?
                              <ResumeFormPortfolioUserPreview portfolId={rowNum}/>
                              :
                              <ResumeFormPortfolioServicePreview portfolId={rowNum}/>
                              }

                          </Paper>
                        </Collapse>
                      </TableCell>
                    </TableRow>
                    </Fragment>
                )})}

              <TableEmptyRows
                height={denseHeight}
                emptyRows={emptyRows(table.page, table.rowsPerPage, dataFiltered.length)}
              />
            </TableBody>
          </Table>
        </Scrollbar>
      </TableContainer>

    </div>
  );
}

// ----------------------------------------------------------------------

function applyFilter({
  inputData,
  comparator,
}: {
  inputData: CustomRowDataType[];
  comparator: (a: any, b: any) => number;
}) {
  const stabilizedThis = inputData.map((el, index) => [el, index] as const);

  stabilizedThis.sort((a, b) => {
    const order = comparator(a[0], b[0]);

    if (order !== 0) return order;

    return a[1] - b[1];
  });

  inputData = stabilizedThis.map((el) => el[0]);

  return inputData;
}
