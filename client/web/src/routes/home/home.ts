import {customElement, FASTElement, observable} from '@microsoft/fast-element';
import {HomeTemplate as template} from './home.template';
import {HomeStyles as styles} from './home.styles';
import {EntityManagement} from '@genesislcap/foundation-entity-management';
import {ZeroGridPro} from '@genesislcap/foundation-zero-grid-pro';
import {Connect} from '@genesislcap/foundation-comms';

EntityManagement; //imported from '@genesislcap/foundation-entity-management' to display Trade grid

const name = 'home-route';

//describes the default config for the grid columns
const defaultColumnConfig = {
  enableCellChangeFlash: true,
  enableRowGroup: true,
  enablePivot: true,
  enableValue: true,
};

//grid columns that will be showed
const COLUMNS = [
  {
    ...defaultColumnConfig,
    field: 'TRADE_ID',
    headerName: 'Id',
  },
  {
    ...defaultColumnConfig,
    field: 'QUANTITY',
    headerName: 'Quantity',
  },
  {
    ...defaultColumnConfig,
    field: 'PRICE',
    headerName: 'Price',
  },
  {
    ...defaultColumnConfig,
    field: 'SYMBOL',
    headerName: 'Symbol',
  },
  {
    ...defaultColumnConfig,
    field: 'DIRECTION',
    headerName: 'Direction',
  },
  {
    ...defaultColumnConfig,
    field: 'TRADE_STATUS',
    headerName: 'Status',
  },
];

@customElement({
  name,
  template,
  styles,
})
export class Home extends FASTElement {
  @observable columns: any = COLUMNS;

  public positionsGrid!: ZeroGridPro;

  @Connect connection: Connect;

  constructor() {
    super();
  }
}