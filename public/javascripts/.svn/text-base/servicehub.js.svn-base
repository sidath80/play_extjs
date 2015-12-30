/*
 *  Service hub application
 */

Ext.application({
    name: 'ServiceHub',
    launch: function() {
        Ext.create('Ext.container.Viewport', {
            layout:'border',
            defaults: {
                collapsible: true,
                split: true
            },
            items: [
                Ext.create('EBuilder.ngetp.servicehub.window.Services'),
                Ext.create('EBuilder.ngetp.servicehub.window.ServiceDetails'),
            ]
        });
    }
});

Ext.define('EBuilder.ngetp.servicehub.window.ServiceDetailsDialog', {
    extend: 'Ext.window.Window',
    title: 'Service Details',
    height: 200,
    width: 400,
    layout: 'fit',
    closable: false,
    items: [
        Ext.create('Ext.form.Panel',
        {
            items: [
                {
                    id: 'serviceDetails.serviceId',
                    xtype: 'hidden',
                    name: 'serviceId'
                },
                {
                    id: 'serviceDetails.description',
                    xtype: 'textfield',
                    fieldLabel: 'Description',
                    name: 'description'
                }
            ],
            buttons: [
                {
                    text: 'Save',
                    handler: function() {
                        this.up('form').getForm().submit({
                            url: 'Registry/setServiceDetails',
                            success:function(form, action) {
                                servicesDetailsWindow.hide();
                            },
                            failure: function(response) {
                                alert('Error');
                            }
                        });
                    }
                },
                {
                    text: 'Cancel',
                    handler: function() {
                        servicesDetailsWindow.hide();
                    }
                }
            ]
        })]
});

var servicesDetailsWindow = null;

function showServiceDetails(response) {
    if(servicesDetailsWindow == null) {
        servicesDetailsWindow = Ext.create('EBuilder.ngetp.servicehub.window.ServiceDetailsDialog');
    }
    var respObj = Ext.decode(response.responseText);
    Ext.getCmp('serviceDetails.serviceId').setValue(respObj.data['serviceId']);
    Ext.getCmp('serviceDetails.description').setValue(respObj.data['description']);
    servicesDetailsWindow.show();
}

function searchServices() {
    var store = Ext.getCmp('servicesGrid').store;
    store.proxy.extraParams.name = Ext.getCmp('servicesForm.name').getValue();
    store.proxy.extraParams.host = Ext.getCmp('servicesForm.host').getValue();
    store.proxy.extraParams.type = Ext.getCmp('servicesForm.type').getValue();
    store.load();
    store.sort();
}

function refreshServiceDetails(reset) {
    var grid = Ext.getCmp('servicesGrid');
    var record = grid.getSelectionModel().getSelection();
    var name = "Not selected";
    var type = "Not selected";
    var serviceId = null;

    if(reset != true) {
       name = record[0].data.name;
       type = record[0].data.type;
       serviceId = record[0].data.serviceId;
    }

    var nameCmp = Ext.getCmp('servicesDetails.name');
    nameCmp.setText(name);
    var typeCmp = Ext.getCmp('servicesDetails.type');
    typeCmp.setText(type);

    var store = Ext.getCmp('nodesGrid').store;
    store.proxy.extraParams.serviceId = serviceId;
    store.load();
    store.sort();

    var propsStore = Ext.getCmp('propertiesGrid').store;
    propsStore.proxy.extraParams.serviceId = serviceId;
    propsStore.load();
    propsStore.sort();
}

var servicePropertyDialog = null;

Ext.define('EBuilder.ngetp.servicehub.window.ServicePropertyDialog', {
    extend: 'Ext.window.Window',
    title: 'Service Properties',
    height: 200,
    width: 400,
    layout: 'fit',
    closable: false,
    items: [
        Ext.create('Ext.form.Panel', {        
            closable: false,
            items: [
                {
                    id: 'serviceProperty.name',
                    xtype: 'textfield',
                    fieldLabel: 'Name',
                    name: 'name',
                    validateOnChange:false,
                    allowBlank:false
                },
                {
                    id: 'serviceProperty.value',
                    xtype: 'textfield',
                    fieldLabel: 'Value',
                    name: 'value',
                    validateOnChange:false,
                    allowBlank:false
                },
                {
                    id: 'serviceProperty.serviceId',
                    xtype: 'hidden',
                    name: 'serviceId'
                }
            ],
            buttons: [
                {
                    text: 'Save',
                    handler: function() {
                        this.up('form').getForm().submit({
                            url: 'Registry/setServiceProperty',
                            success:function(form, action) {
                                servicePropertyDialog.hide();
                                Ext.getCmp('propertiesGrid').store.load();
                            },
                            failure: function(response) {
                                alert('Error');
                            }
                        });
                    }
                },
                {
                    text: 'Cancel',
                    handler: function() {
                        servicePropertyDialog.hide();
                    }
                }
            ]
        })
    ]
});


Ext.define('EBuilder.ngetp.servicehub.window.ServicesForm', {
    extend: 'Ext.form.Panel',
    defaults: {
        margin :'10 10 10 10'
    },
    items: [
        {
            id: 'servicesForm.name',
            xtype: 'textfield',
            fieldLabel: 'Name',
            name: 'name'
        },
        {
            id: 'servicesForm.host',
            xtype: 'textfield',
            fieldLabel: 'Host',
            name: 'host'
        },
        {
            id: 'servicesForm.type',
            name: 'type',
            xtype: 'combo',
            editable:false,            
            width: 50,
            fieldLabel: 'Type',
            displayField:'name',
            valueField: 'value',
            store: Ext.create('Ext.data.Store',
            {
                fields: ['name', 'value'],
                data : [
                    { name: 'RPC', value: 'RPC' },
                    { name: 'REST', value: 'REST' },
                    { name: 'All', value: 'All' },
                ],
                sorters: [
                    {
                        property: 'name'
                    }
                ]
            })
        },
        {
            id: 'servicesForm.submit',
            xtype: 'button',
            text: 'Search',
            handler: function() {
                searchServices();
                refreshServiceDetails(true);                
            }
        },
    ]
});

Ext.define('eBuilder.ngetp.servicehub.window.ServiceDataModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'serviceId', type: 'string'},
        {name: 'groupId', type: 'string'},
        {name: 'artifactId', type: 'string'},
        {name: 'version', type: 'string'},
        {name: 'name', type: 'string'},
        {name: 'type', type: 'string'},
    ]
});

Ext.define('EBuilder.ngetp.servicehub.window.ServicesGrid', {
    extend : 'Ext.grid.Panel',
    store: Ext.create('Ext.data.Store', {
        model: 'eBuilder.ngetp.servicehub.window.ServiceDataModel',
        proxy: {
            extraParams : {},
            type: 'ajax',
            url: 'Registry/search',
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    }),
    columns: [
        {
            text     : 'Name',
            flex     : 1,
            sortable : false,
            dataIndex: 'name',
            renderer: function(val, meta, record) {
                return record.data.groupId + '-' + record.data.artifactId
                        + '-' + record.data.version + '-' + record.data.name;
            }
        }
    ],
    height: 1000,
    width: 400,
    listeners: {
        itemclick: function(view, record, item, index) {
            refreshServiceDetails();
        }
    }
});

Ext.define('EBuilder.ngetp.servicehub.window.Services',
{
    extend : 'Ext.panel.Panel',
    title: 'Services',
    region:'west',
    width: 400,
    items: [
        Ext.create('EBuilder.ngetp.servicehub.window.ServicesForm'),
        Ext.create('EBuilder.ngetp.servicehub.window.ServicesGrid', { id: 'servicesGrid' }),
    ]
}
);

Ext.define('eBuilder.ngetp.servicehub.window.ServicePropertiesDataModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'name', type: 'string'},
        {name: 'value', type: 'string'},
    ]
});

Ext.define('EBuilder.ngetp.servicehub.window.ServicesPropertiesGrid', {
    extend : 'Ext.grid.Panel',
    store: Ext.create('Ext.data.Store', {
        model: 'eBuilder.ngetp.servicehub.window.ServicePropertiesDataModel',
        proxy: {
            extraParams : {},
            type: 'ajax',
            url: 'Registry/getServiceProperties',
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    }),
    columns: [
        {
            text     : 'Name',
            sortable : false,
            dataIndex: 'name'
        },
        {
            text     : 'Value',
            sortable : false,
            dataIndex: 'value'
        }
    ],
    height: 100,
    width: 300
});

Ext.define('eBuilder.ngetp.servicehub.window.ServiceNodesDataModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'host', type: 'string'},
        {name: 'port', type: 'string'},
    ]
});

Ext.define('EBuilder.ngetp.servicehub.window.ServiceNodesGrid', {
    extend : 'Ext.grid.Panel',
    store: Ext.create('Ext.data.Store', {
        model: 'eBuilder.ngetp.servicehub.window.ServiceNodesDataModel',
        proxy: {
            extraParams : {},
            type: 'ajax',
            url: 'Registry/nodes',
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    }),
    columns: [
        {
            text     : 'Host',
            sortable : false,
            dataIndex: 'host'
        },
        {
            text     : 'Port',
            sortable : false,
            dataIndex: 'port'
        }
    ],
    height: 200,
    width: 300
});


Ext.define('EBuilder.ngetp.servicehub.window.ServiceMethodsDialog', {
    extend: 'Ext.window.Window',
    title: 'Service Methods',
    height: 200,
    width: 300,
    layout: 'fit',
    closable: false,
    items: [
        Ext.create('Ext.form.Panel', {
            id: 'serviceMethodsForm',
            closable: false,
            defaults: {
                margin :'2 2 2 2'
            },            
            items: [
                {
                    id: 'serviceMethods.serviceId',
                    xtype: 'hidden',
                    name: 'serviceId'
                },
                {
                    id: 'serviceMethods.names',
                    xtype: 'radiogroup',
                    columns: 1                    
                }
            ],
            buttons: [
                {
                    text: 'Select',
                    handler: function() {
                        this.up('form').getForm().submit({
                            url: 'Registry/getServiceMethodArgs',
                            success:function(form, action) {
                                serviceMethodsDialog.hide();
                                if(serviceMethodArgsDialog == null) {
                                    serviceMethodArgsDialog = Ext.create(
                                        'EBuilder.ngetp.servicehub.window.ServiceMethodArgsDialog',
                                        {id: 'methodArgsDialog'})
                                }
                                Ext.getCmp('serviceMethodArgs.serviceId').setValue(
                                        Ext.getCmp('serviceMethods.serviceId').getValue());
                                Ext.getCmp('serviceMethodArgs.methodName').setValue(
                                        Ext.getCmp('serviceMethods.names').getValue().methodName);

                                var panel = Ext.getCmp('serviceMethodArgs.methodArgs');
                                var respObj = Ext.decode(action.response.responseText);
                                panel.removeAll();
                                respObj.data.forEach(function(value, index) {
                                    panel.add({
                                        xtype: 'textfield',
                                        fieldLabel : value,
                                        name: 'methodValues'
                                    });
                                });
                                panel.doLayout();

                                serviceMethodArgsDialog.show();
                            },
                            failure: function(response) {
                                Ext.MessageBox.alert("Error", "Cannot get service method arguments");
                            }
                        });
                    }
                },
                {
                    text: 'Cancel',
                    handler: function() {
                        serviceMethodsDialog.hide();
                    }
                }
            ]
        })
    ],
    listeners: {
        show : function() {
            var grid = Ext.getCmp('servicesGrid');
            var record = grid.getSelectionModel().getSelection();
            if(record.length != 0) {
                var serviceId = record[0].data.serviceId;
                Ext.getCmp('serviceMethods.serviceId').setValue(serviceId);
            }

            var panel = Ext.getCmp('serviceMethods.names');
            panel.removeAll();

            Ext.Ajax.request({
                url: 'Registry/getServiceMethods',
                method: 'POST',
                params: {
                    serviceId: serviceId
                },
                success: function(response) {
                    var respObj = Ext.decode(response.responseText);
                    var panel = Ext.getCmp('serviceMethods.names');
                    panel.removeAll();
                    respObj.data.forEach(function(value, index) {
                       panel.add({
                           xtype: 'radio',
                           name: 'methodName',
                           boxLabel: value,
                           inputValue: value ,
                           checked : (index == 0 ? true : false)
                       });
                    });
                    panel.doLayout();
                },
                failure: function(response) {
                    serviceMethodsDialog.hide();
                    Ext.MessageBox.alert("Error", "Cannot get service methods");                    
                }
            });
        }
    }
});

var serviceMethodsDialog = null;



Ext.define('EBuilder.ngetp.servicehub.window.ServiceMethodArgsDialog', {
    extend: 'Ext.window.Window',
    title: 'Service Method Args',
    height: 200,
    width: 300,
    layout: 'fit',
    closable: false,
    items: [
        Ext.create('Ext.form.Panel', {
            id: 'serviceMethodArgsForm',
            closable: false,
            defaults: {
                margin :'2 2 2 2'
            },
            items: [
                {
                    id: 'serviceMethodArgs.serviceId',
                    xtype: 'hidden',
                    name: 'serviceId'
                },
                {
                    id: 'serviceMethodArgs.methodName',
                    xtype: 'hidden',
                    name: 'methodName'
                },
                {
                    id: 'serviceMethodArgs.methodArgs',
                    xtype: 'fieldset',
                    columns: 1
                }
            ],
            buttons: [
                {
                    text: 'Invoke',
                    handler: function() {
                        this.up('form').getForm().submit({
                            url: 'Registry/callServiceMethod',
                            success:function(form, action) {
                                serviceMethodArgsDialog.hide();
                                var respObj = Ext.decode(action.response.responseText);
                                Ext.MessageBox.alert("Result", respObj.data);
                            },
                            failure: function(response) {
                                Ext.MessageBox.alert("Error", "Cannot call service method");
                            }
                        });
                    }
                },
                {
                    text: 'Cancel',
                    handler: function() {
                        serviceMethodArgsDialog.hide();
                    }
                }
            ]
        })
    ],
    listeners: {
        show : function() {
            
        }
    }
});

var serviceMethodArgsDialog = null;



Ext.define('EBuilder.ngetp.servicehub.window.ServiceDetails',
{
    extend : 'Ext.panel.Panel',
    title: 'Details',
    region:'center',
    layout: {
        type: 'vbox'
    },
    items: [
        {
            xtype: 'container',
            defaults: {
                margin :'10 10 10 10'
            },
            items: [
                {
                    xtype: 'label',
                    text: 'Name:'
                },
                {
                    id: 'servicesDetails.name',
                    xtype: 'label',
                    text: 'Not Selected',
                    width: 200
                },
                {
                    id: 'servicesDetails.edit',
                    xtype: 'button',
                    text: 'Edit',
                    handler: function() {
                        var grid = Ext.getCmp('servicesGrid');
                        var record = grid.getSelectionModel().getSelection();
                        if(record.length != 0) {
                            Ext.Ajax.request({
                                url: 'Registry/getServiceDetails',
                                method: 'POST',
                                params: {
                                    serviceId: record[0].data.serviceId
                                },
                                success: function(response) {
                                    showServiceDetails(response);
                                },
                                failure: function() {
                                    alert("Error");
                                }
                            });
                        } else {
                            alert("Select service first");
                        }
                    }

                },
                {
                    id: 'servicesDetails.clone',
                    xtype: 'button',
                    text: 'Clone',
                    handler: function() {
                        Ext.Msg.prompt("", 'Clone name', function(btn, text) {
                            if (btn == 'ok'){
                                var grid = Ext.getCmp('servicesGrid');
                                var record = grid.getSelectionModel().getSelection();
                                if(record.length != 0) {
                                    Ext.Ajax.request({
                                        url: 'Registry/cloneService',
                                        method: 'POST',
                                        params: {
                                            serviceId: record[0].data.serviceId,
                                            serviceName: text
                                        },
                                        success: function(response) {
                                            searchServices();
                                            refreshServiceDetails(true);

                                        },
                                        failure: function() {
                                            alert("Error");
                                        }
                                    });
                                } else {
                                    alert("Select service first");
                                }

                            }
                        });
                    }
                },
                {
                    id: 'servicesDetails.delete',
                    xtype: 'button',
                    text: 'Delete',
                    handler: function() {
                        Ext.Msg.show({
                            title:'Delete',
                            msg: 'Delete service?',
                            buttons: Ext.Msg.YESNO,
                            fn: function(btn) {
                                if (btn == 'yes') {
                                    var grid = Ext.getCmp('servicesGrid');
                                    var record = grid.getSelectionModel().getSelection();
                                    if(record.length != 0) {
                                        Ext.Ajax.request({
                                            url: 'Registry/disableService',
                                            method: 'POST',
                                            params: {
                                                serviceId: record[0].data.serviceId
                                            },
                                            success: function(response) {
                                                searchServices();
                                                refreshServiceDetails(true);

                                            },
                                            failure: function() {
                                                alert("Error");
                                            }
                                        });
                                    } else {
                                        alert("Select service first");
                                    }

                                }
                            },
                            icon: Ext.window.MessageBox.QUESTION
                        });

                    }
                },
            ]
        },
        {
            xtype: 'container',
            defaults: {
                margin :'10 10 10 10'
            },
            items: [
                {
                    xtype: 'label',
                    text: 'Type:'
                },
                {
                    id: 'servicesDetails.type',
                    xtype: 'label',
                    text: 'Not Selected'
                },
                {
                    id: 'servicesDetails.testHttpClient',
                    xtype: 'button',
                    text: 'Test HTTP Client',
                    handler: function() {
                        var grid = Ext.getCmp('servicesGrid');
                        var record = grid.getSelectionModel().getSelection();
                        if(record.length != 0) {
                            if(serviceMethodsDialog == null) {
                                serviceMethodsDialog = Ext.create('EBuilder.ngetp.servicehub.window.ServiceMethodsDialog', {id: 'methodsDialog'})
                            }
                            serviceMethodsDialog.show();
                        }
                    }
                },
                {
                    id: 'servicesDetails.downloadJavaClient',
                    xtype: 'button',
                    text: 'Download Java Client',
                    handler: function() {
                        var grid = Ext.getCmp('servicesGrid');
                        var record = grid.getSelectionModel().getSelection();
                        if(record.length != 0) {
                            Ext.Ajax.request({
                                url: 'Registry/getJavaClientUrl',
                                method: 'POST',
                                params: {
                                    serviceId: record[0].data.serviceId
                                },
                                success: function(response) {
                                    var respObj = Ext.decode(response.responseText);
                                    window.open(respObj.data);
                                },
                                failure: function(response) {
                                    alert("Error" + response);
                                }
                            });

                        }
                    }
                }
             ]
        },
        {
            xtype: 'container',            
            defaults: {
                margin :'10 10 10 10'
            },            
            items: [
                {
                    xtype: 'label',
                    text: 'Details'
                },
                {
                    id: 'servicesDetails.servicePropertyNew',
                    xtype: 'button',
                    text: 'New',
                    handler: function() {
                        if(servicePropertyDialog == null) {
                            servicePropertyDialog = Ext.create('EBuilder.ngetp.servicehub.window.ServicePropertyDialog', {id: 'propertyDialog'})
                        }
                        var grid = Ext.getCmp('servicesGrid');
                        var record = grid.getSelectionModel().getSelection();
                        if(record.length != 0) {
                            Ext.getCmp('serviceProperty.serviceId').setValue(record[0].data.serviceId);
                            Ext.getCmp('serviceProperty.name').setValue('');
                            Ext.getCmp('serviceProperty.value').setValue('');                                                            
                            servicePropertyDialog.show();

                        } else {
                            Alert('Select service first')
                        }

                    }
                },
                {
                    id: 'servicesDetails.servicePropertyEdit',
                    xtype: 'button',
                    text: 'Edit',
                    handler: function() {
                        if(servicePropertyDialog == null) {
                            servicePropertyDialog = Ext.create('EBuilder.ngetp.servicehub.window.ServicePropertyDialog', {id: 'propertyDialog'})
                        }
                        var grid = Ext.getCmp('servicesGrid');
                        var record = grid.getSelectionModel().getSelection();
                        if(record.length != 0) {
                            Ext.getCmp('serviceProperty.serviceId').setValue(record[0].data.serviceId);

                            var propGrid = Ext.getCmp('propertiesGrid');
                            var propRecord = propGrid.getSelectionModel().getSelection();
                            if(propRecord.length != 0) {
                                Ext.getCmp('serviceProperty.name').setValue(propRecord[0].data.name);
                                Ext.getCmp('serviceProperty.value').setValue(propRecord[0].data.value);
                            }
                            servicePropertyDialog.show();

                        } else {
                            Alert('Select service first')
                        }

                    }
                }
             ]
        },
        {
            items: [
                Ext.create('EBuilder.ngetp.servicehub.window.ServicesPropertiesGrid', { id: 'propertiesGrid' }),
            ]
        },
        {
            xtype: 'container',
            defaults: {
                margin :'10 10 10 10'
            },             
            items: [
                {
                    xtype: 'label',
                    text: 'Nodes'
                }
            ]
        },
        {
            items: [
                Ext.create('EBuilder.ngetp.servicehub.window.ServiceNodesGrid', { id: 'nodesGrid' }),
            ]
        }
    ]}
);