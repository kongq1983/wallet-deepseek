import * as React from 'react';
import { cn } from '../lib/utils';

/** 表格外层容器：宽度超出时横向滚动。 */
const TableContainer = React.forwardRef<HTMLDivElement, React.HTMLAttributes<HTMLDivElement>>(
  ({ className, ...props }, ref) => (
    <div ref={ref} className={cn('relative w-full overflow-x-auto', className)} {...props} />
  ),
);
TableContainer.displayName = 'TableContainer';

const Table = React.forwardRef<HTMLTableElement, React.HTMLAttributes<HTMLTableElement>>(
  ({ className, ...props }, ref) => (
    <table ref={ref} className={cn('w-full caption-bottom text-sm', className)} {...props} />
  ),
);
Table.displayName = 'Table';

const TableHeader = React.forwardRef<HTMLTableSectionElement, React.HTMLAttributes<HTMLTableSectionElement>>(
  ({ className, ...props }, ref) => (
    <thead ref={ref} className={cn('[&_tr]:border-b [&_tr]:border-border', className)} {...props} />
  ),
);
TableHeader.displayName = 'TableHeader';

const TableBody = React.forwardRef<HTMLTableSectionElement, React.HTMLAttributes<HTMLTableSectionElement>>(
  ({ className, ...props }, ref) => (
    <tbody ref={ref} className={cn('[&_tr:last-child]:border-0', className)} {...props} />
  ),
);
TableBody.displayName = 'TableBody';

const TableRow = React.forwardRef<HTMLTableRowElement, React.HTMLAttributes<HTMLTableRowElement>>(
  ({ className, ...props }, ref) => (
    <tr
      ref={ref}
      className={cn('border-b border-border transition-colors hover:bg-muted/50', className)}
      {...props}
    />
  ),
);
TableRow.displayName = 'TableRow';

const TableHead = React.forwardRef<HTMLTableCellElement, React.ThHTMLAttributes<HTMLTableCellElement>>(
  ({ className, ...props }, ref) => (
    <th
      ref={ref}
      className={cn(
        'h-10 whitespace-nowrap px-3 text-left align-middle text-sm font-medium text-muted-foreground',
        className,
      )}
      {...props}
    />
  ),
);
TableHead.displayName = 'TableHead';

const TableCell = React.forwardRef<HTMLTableCellElement, React.TdHTMLAttributes<HTMLTableCellElement>>(
  ({ className, ...props }, ref) => (
    <td ref={ref} className={cn('px-3 py-2 align-middle', className)} {...props} />
  ),
);
TableCell.displayName = 'TableCell';

/** 表格空态：无数据时统一展示。 */
const TableEmpty = ({ colSpan, text = '暂无数据' }: { colSpan: number; text?: string }) => (
  <TableRow className="hover:bg-transparent">
    <TableCell colSpan={colSpan} className="h-24 text-center text-muted-foreground">
      {text}
    </TableCell>
  </TableRow>
);

export { Table, TableBody, TableCell, TableContainer, TableEmpty, TableHead, TableHeader, TableRow };
