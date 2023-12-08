import {html, repeat, when, ref} from '@microsoft/fast-element';
import type {Home} from './home';
import { tradeFormCreateSchema, tradeFormUpdateSchema } from './schemas';

export const positionsColumnDefs: any[] = [
    {field: 'INSTRUMENT_ID', headerName: 'Instrument'},
    {field: 'QUANTITY', headerName: 'Quantity'},
    {field: 'NOTIONAL', headerName: 'Notional'},
    {field: 'VALUE', headerName: 'Value'},
    {field: 'PNL', headerName: 'Pnl'},
];

export const HomeTemplate = html<Home>`
<div class="split-layout">
    <div class="top-layout">
        <entity-management
          resourceName="ALL_TRADES"
          title = "Trades"
          entityLabel="Trades"
          createEvent = "EVENT_TRADE_INSERT"
          updateEvent = "EVENT_TRADE_MODIFY"
          deleteEvent = "EVENT_TRADE_CANCELLED"
          :columns=${x => x.columns}
          :createFormUiSchema=${() => tradeFormCreateSchema}
          :updateFormUiSchema=${() => tradeFormUpdateSchema}
        ></entity-management>
    </div>
    <div class="top-layout">
        <zero-card class="positions-card">
            <span class="card-title">Positions</span>
            <zero-grid-pro ${ref('positionsGrid')} rowHeight="45" only-template-col-defs>
                ${when(x => x.connection.isConnected, html`
                  <grid-pro-genesis-datasource resource-name="ALL_POSITIONS"></grid-pro-genesis-datasource>
                  ${repeat(() => positionsColumnDefs, html`
                    <grid-pro-column :definition="${x => x}" />
                  `)}
                `)}
            </zero-grid-pro>
        </zero-card>
    </div>
</div>
`;